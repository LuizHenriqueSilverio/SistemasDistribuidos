package sd.xml;

import java.io.File;
import com.thoughtworks.xstream.XStream;
import sd.pojos.Address;
import sd.pojos.Person;

public class XMLReader {
	public static void main(String[] args) {
		XStream xstream = new XStream();
		// Ajuste de segurança do XStream
		Class<?>[] classes = new Class[] { Person.class, Address.class };
		xstream.allowTypes(classes);
		Person emerson = null;
		File xmlFile = new File("person.xml");
		emerson = (Person) xstream.fromXML(xmlFile);
		if (emerson != null) {
			System.out.println(emerson);
			System.out.println(emerson.getAddress());
		}
	}
}
