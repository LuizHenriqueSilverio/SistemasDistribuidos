
public class Main {

	public static void main(String[] args) {
		Cube cubo = new Cube();
		
		Producer produtor = new Producer(cubo);
		produtor.setName("Produtor");

		
		Consumer consumidor = new Consumer(cubo);
		consumidor.setName("Consumidor");
		
		produtor.start();
		consumidor.start();
		
		System.out.println("Fim thread 'main'.");
	}

}
