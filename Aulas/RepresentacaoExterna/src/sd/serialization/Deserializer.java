package sd.serialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import sd.pojos.Person;

public class Deserializer {
	public static void main(String[] args) {
		InputStream file = null;
		ObjectInput objectInput = null;
		Person emerson = null;
		try {
			file = new FileInputStream("person.ser");
			objectInput = new ObjectInputStream(file);
			emerson = (Person) objectInput.readObject();
		} catch (Exception e) {
			System.out.println("Objeto não pode ser deserializado.");
			e.printStackTrace();
		} finally {
			try {

				if (file != null)
					file.close();
				if (objectInput != null)
					objectInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (emerson != null) {
			System.out.println(emerson);
			System.out.println(emerson.getAddress());
		}
	}
}