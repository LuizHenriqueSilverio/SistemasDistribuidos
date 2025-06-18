package sd.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import com.cedarsoftware.util.io.JsonWriter;
import sd.pojos.Address;
import sd.pojos.Person;

public class JSONWriter {
	public static void main(String[] args) {
		PrintWriter writer = null;
		Address address = new Address("Rua José", 20, "Por do Sol", "37.130-000", "Alfenas", "MG");
		Person emerson = new Person("Emerson Carvalho", "060.793.477-11", new Date(), address);
		try {
			String json = JsonWriter.objectToJson(emerson);
			writer = new PrintWriter("person.json");
			writer.println(json);
			System.out.println("Objeto parseado com sucesso.");
		} catch (IOException e) {
			System.out.println("Objeto n ̃ao pode ser parseado.");
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
