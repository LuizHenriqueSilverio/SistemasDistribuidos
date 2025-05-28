
public class Producer extends Thread{
	
	private Cube cubo;
	
	public Producer(Cube cubo) {
		this.cubo = cubo;
	}
	
	@Override
	public void run() {
		while (true) {	
			synchronized (cubo) {
				if (cubo.getSomeToConsume()) {
					try {
						cubo.wait();
					} catch (InterruptedException e) {}
				}
				
				cubo.put( (int) (Math.random() * 100));
				cubo.notify();
			}
			
		}
	}
	
	
}
