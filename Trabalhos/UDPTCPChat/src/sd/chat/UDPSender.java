package sd.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class UDPSender implements Sender {
	private DatagramSocket senderSocket = null;
	private InetAddress receiver;
	private int receiverPort;

	public UDPSender(InetAddress receiver, int receiverPort) throws ChatException {
		validateAttributes(receiver, receiverPort);
		this.receiver = receiver;
		this.receiverPort = receiverPort;
		try {
			this.senderSocket = new DatagramSocket();
		} catch (SocketException socketException) {
			throw new ChatException("Houve algum erro ao iniciar o sender.", socketException);
		}
	}

	public void send(String message) throws ChatException {
		byte[] messageBytes = message.getBytes();
		DatagramPacket datagramPacketToSend = new DatagramPacket(messageBytes, messageBytes.length, receiver, receiverPort);
		try {
			senderSocket.send(datagramPacketToSend);
		} catch (IOException ioException) {
			throw new ChatException(String.format("Sua mensagem ’%s’ não foi enviada.", message), ioException);
		}
	}
	
	@Override
    public void close() {
        if (senderSocket != null && !senderSocket.isClosed()) {
            senderSocket.close();
        }
    }

	private void validateAttributes(InetAddress receiver, int receiverPort) {
		if (receiver == null)
			throw new IllegalArgumentException("O receiver não pode ser nulo.");
		if (receiverPort <= 0)
			throw new IllegalArgumentException(String.format("Porta inválida para o receiver."));
	}
}