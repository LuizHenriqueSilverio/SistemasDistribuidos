package sd.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

class UDPReceiver implements Receiver {
	private static int MIN_BUFFER_SIZE = 1000;
	private static int MIN_PORT_NUMBER = 1024;
	private int portNumber;
	private int bufferSize;
	private DatagramSocket receiverSocket = null;
	private byte[] incomingBuffer = null;
	private boolean isRunning = false;
	private MessageContainer container;

	public UDPReceiver(int portNumber, int bufferSize, MessageContainer container) throws ChatException {
		validateAttributes(portNumber, bufferSize, container);
		asignAttributes(portNumber, bufferSize, container);
		try {
			prepare();
		} catch (SocketException socketException) {
			throw new ChatException("Houve algum erro ao iniciar o receiver.", socketException);
		}
		new Thread(this).start();
	}

	public void run() {
		isRunning = true;
		while (isRunning) {
			try {
				receive();
			} catch (IOException ioException) {
                if (isRunning) {
				    container.newMessage("Houve algum erro ao receber mensagens.");
                }
			}
		}
	}
	
	@Override
	public void close() {
		isRunning = false;
		if (receiverSocket != null && !receiverSocket.isClosed()) {
			receiverSocket.close();
		}
	}

	private void validateAttributes(int portNumber, int bufferSize, MessageContainer container) {
		if (portNumber <= MIN_PORT_NUMBER)
			throw new IllegalArgumentException("O receiver não pode usar portas reservadas.");
		if (container == null)
			throw new IllegalArgumentException("O container de mensagens não pode ser nulo.");
		if (bufferSize < MIN_BUFFER_SIZE)
			throw new IllegalArgumentException(String.format("O tamanho do buffer precisa ser maior que %d", MIN_BUFFER_SIZE));
	}

	private void asignAttributes(int portNumber, int bufferSize, MessageContainer container) {
		this.portNumber = portNumber;
		this.bufferSize = bufferSize;
		this.container = container;
	}

	private void prepare() throws SocketException {
		receiverSocket = new DatagramSocket(portNumber);
	}

	private void receive() throws IOException {
		incomingBuffer = new byte[bufferSize];
		DatagramPacket received = new DatagramPacket(incomingBuffer, incomingBuffer.length);
		receiverSocket.receive(received);
		container.newMessage(new String(received.getData()));
	}
}