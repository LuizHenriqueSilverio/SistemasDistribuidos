
public class Cube {
	private int value;
	private boolean someToConsume = false;
	
	public int get() {
		System.err.printf("Consumidor consumiu %d\n", value);
		someToConsume = false;
		return value;
	}
	
	public void put(int v) {
		System.out.printf("Produtor produziu %d\n", value);
		someToConsume = true;
		this.value = v;
	}
	
	public boolean getSomeToConsume() {
		return someToConsume;
	}
	
	public void setSomeToConsume(boolean someToConsume) {
		this.someToConsume = someToConsume;
	}
}
