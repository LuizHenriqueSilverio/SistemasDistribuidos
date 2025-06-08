
public class Consumer extends Thread{

	private Cube cubo;

	public Consumer (Cube cubo) {
		this.cubo = cubo;
	}

	@Override
	public void run() {
		while (true) {
			
			synchronized (cubo) {
				if (!cubo.getSomeToConsume()) {
					try {
						cubo.wait();
					} catch (InterruptedException e) {}
				}
				
				cubo.get();
				cubo.notify();
			}
			
		}
	}
}
