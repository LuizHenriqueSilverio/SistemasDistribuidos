
public class Consumer extends Thread{

	private Cube cubo;

	public Consumer (Cube cubo) {
		this.cubo = cubo;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			
			try {
				Lock.lock.acquire();
				
				cubo.get();
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			finally {
				Lock.lock.release();
			}
			
		}
	}
}
