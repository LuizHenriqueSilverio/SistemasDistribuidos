
public class Main {

	public static void main(String[] args) {
		RandomA ra = new RandomA(1);
		RandomB rb = new RandomB(2);
		RandomA ra2 = new RandomA(3);
		RandomB rb2 = new RandomB(4);
		
		ra.start();
		rb.start();
		ra2.start();
		rb2.start();
	}

}
