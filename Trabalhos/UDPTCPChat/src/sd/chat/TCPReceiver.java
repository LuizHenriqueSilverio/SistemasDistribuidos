package sd.chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiver implements Receiver {
	private int portNumber;
	private MessageContainer container;
	private ServerSocket receiverSocket;
	private Socket socket;
	private DataInputStream inputFlow;
	private volatile boolean isRunning = false;

	public TCPReceiver(int portNumber, MessageContainer container) throws ChatException {
		this.portNumber = portNumber;
		this.container = container;

		try {
			prepare();
		} catch (IOException ioException) {
			throw new ChatException("Houveram erros ao iniciar o receiver: ", ioException);
		}
		new Thread(this).start();
	}

	private void prepare() throws IOException {
		this.receiverSocket = new ServerSocket(this.portNumber);
	}

	public void run() {
        isRunning = true;
		try {
			this.socket = this.receiverSocket.accept(); 
            if(isRunning) {
			    this.inputFlow = new DataInputStream(this.socket.getInputStream());
			    while (isRunning) {
				    receive();
			    }
            }
		} catch (IOException ioException) {
            if(isRunning) {
			    container.newMessage("A conexão foi encerrada.");
            }
		} finally {
            close();
        }
	}

	private void receive() throws IOException {
		String message = this.inputFlow.readUTF();
		this.container.newMessage(message);
	}
	
	@Override
    public void close() {
        isRunning = false;
        try {
            if (receiverSocket != null && !receiverSocket.isClosed()) { 
            	receiverSocket.close();
            }
            if (socket != null && !socket.isClosed()) { 
            	socket.close();
            }
            if (inputFlow != null) {
            	inputFlow.close();
            }	
        } catch (IOException e) {}
    }
}
