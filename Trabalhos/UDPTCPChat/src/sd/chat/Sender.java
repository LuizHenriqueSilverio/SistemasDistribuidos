package sd.chat;

public interface Sender {
	void send(String message) throws ChatException;
	void close();
}