package sd.serialization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import sd.pojos.Address;
import sd.pojos.Person;

public class Serializer {
	public static void main(String[] args) {
		OutputStream file = null;
		ObjectOutput objectOutput = null;
		Address address = new Address("Rua José", 20, "Por do Sol", "37.130-000", "Alfenas", "MG");
		Person emerson = new Person("Emerson Carvalho", "060.793.477-11", new Date(), address);
		try {
			file = new FileOutputStream("person.ser");
			objectOutput = new ObjectOutputStream(file);
			objectOutput.writeObject(emerson);

			System.out.println("Objeto serializado com sucesso.");
		} catch (Exception e) {
			System.out.println("Objeto não pode ser serializado.");
			e.printStackTrace();
		} finally {
			try {
				if (file != null)
					file.close();
				if (objectOutput != null)
					objectOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
