package sd.chat;

public interface Receiver extends Runnable {
	void run();
	void close();
}