public class Producer extends Thread{
	
	private Cube cubo;
	
	public Producer(Cube cubo) {
		this.cubo = cubo;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			
			try {
				Lock.lock.acquire();
				
				cubo.put( (int) (Math.random() * 100));
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			finally {
				Lock.lock.release();
			}
		}
	}
}
