
public class Main {

	public static void main(String[] args) {
		Printer printer = new Printer();
		
		PrinterThread job1 = new PrinterThread(printer);
		PrinterThread job2 = new PrinterThread(printer);
		PrinterThread job3 = new PrinterThread(printer);
		PrinterThread job4 = new PrinterThread(printer);
		PrinterThread job5 = new PrinterThread(printer);
		
		job1.start();
		job2.start();
		job3.start();
		job4.start();
		job5.start();
	}

}
