package net.unibld.core.util.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.commons.io.FileUtils;





/**
 * A utility class for Java-XML binding (JAXB)
 * 
 * @author andor
 * 
 */
public class JAXBUtil {
	static class LogValidationEventHandler implements ValidationEventHandler {
		private boolean valid = true;

		@Override
		public boolean handleEvent(ValidationEvent event) {
			if (event.getSeverity() > ValidationEvent.WARNING) {
				System.out.println("Validation error: " + event.getMessage());
				event.getLinkedException().printStackTrace();
				valid = false;
				return false;
			} else {
				System.out.println("Validation warning: " + event.getMessage());
				return true;
			}

		}

		/**
		 * @return the valid
		 */
		public boolean isValid() {
			return valid;
		}

	}

	/**
	 * Validates an object using marshaling
	 * 
	 * @param o
	 *            Object to validate
	 * @return True if valid
	 * @throws Exception 
	 * 
	 */
	public static boolean validateObject(Object o) throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Marshaller m = null;
		try {

			m = JAXBPool.getInstance().borrowMarshaller(o.getClass());

			m.marshal(o, out);

			byte[] bytes = out.toByteArray();

			return validate(o.getClass(), bytes);
		} finally {
			JAXBPool.getInstance().returnMarshaller(o.getClass(), m);
		}
	}

	private static boolean validate(Class<?> contextClass, byte[] bytes) throws Exception {
		Unmarshaller um = null;
		try {

			um = JAXBPool.getInstance().borrowUnmarshaller(contextClass);

			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			LogValidationEventHandler logHandler = new LogValidationEventHandler();
			um.setEventHandler(logHandler);
			um.unmarshal(in);
			return logHandler.isValid();
		} finally {
			JAXBPool.getInstance().returnUnmarshaller(contextClass, um);
		}
	}

	/**
	 * Validates an XML string
	 * 
	 * @param klazz
	 *            Entity class
	 * @param xml
	 *            XML string
	 * @param encoding
	 *            Encoding
	 * @return True if valid
	 * @throws Exception 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static boolean validateXml(Class klazz, String xml, String encoding) throws Exception {
		return validate(klazz, xml.getBytes(encoding));
	}

	
	/**
	 * Unmarshals the specified XML content to the given type
	 * @param <T> Type of the object to unmarshal content into
	 * @param klazz Class of the object to unmarshal content into
	 * @param xmlContent XML content
	 * @return Unmarshalled object
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(Class<T> klazz, String xmlContent) throws Exception {
		String escaped = xmlContent.replace("\u0013", "");
		Unmarshaller um = null;
		try {
			um = JAXBPool.getInstance().borrowUnmarshaller(klazz);

			// Unmarshall the file into a content object
			StringReader rdr = new StringReader(escaped);
			T unmarshalledElement = (T) um.unmarshal(rdr);

			return unmarshalledElement;
		} finally {
			JAXBPool.getInstance().returnUnmarshaller(klazz, um);
		}
	}
	
	
	/**
	 * Unmarshals an object from an XML file
	 * 
	 * @param klazz
	 *            Class to unmarshal to
	 * @param xmlFile
	 *            XML file
	 * @return Unmarshaled object
	 * @throws Exception 
	 * 
	 */
	public static Object unmarshalFromFile(Class<?> klazz, String xmlFile) throws Exception {
		Unmarshaller um = null;
		try {
			um = JAXBPool.getInstance().borrowUnmarshaller(klazz);

			// Unmarshall the file into a content object
			return um.unmarshal(new File(xmlFile));
		} finally {
			JAXBPool.getInstance().returnUnmarshaller(klazz, um);
		}
	}

	/**
	 * Unmarshals an object from an {@link InputStream}
	 * 
	 * @param klazz
	 *            Class to unmarshal to
	 * @param in
	 *            The {@link InputStream} to read from
	 * @return Unmarshaled object
	 * @throws Exception 
	 * 
	 */
	public static Object unmarshalFromStream(Class<?> klazz, InputStream in) throws Exception {
		Unmarshaller um = null;
		try {

			um = JAXBPool.getInstance().borrowUnmarshaller(klazz);

			// Unmarshall the file into a content object
			return um.unmarshal(in);
		} finally {
			JAXBPool.getInstance().returnUnmarshaller(klazz, um);
		}
	}

	public static void marshalToStream(Object o, OutputStream out,boolean formatted) throws Exception {
		if (o==null) {
			throw new NullPointerException("Object to marshal was null");
		}
		Marshaller m = null;
		try {

			m = JAXBPool.getInstance().borrowMarshaller(o.getClass());
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					   new Boolean(formatted));

			m.marshal(o, out);

		} finally {
			JAXBPool.getInstance().returnMarshaller(o.getClass(),m);
		}
	}

	
	/**
	 * Marshals the given object to an XML file
	 * @param o Object to serialize
	 * @param file XML file 
	 * @throws Exception
	 */
	public static void marshallToFile(Object o, File file) throws Exception {
		marshallToFile(o, file, false);
	}
	public static void marshallToFile(Object o, File file,boolean formatted) throws Exception {
		if (o==null) {
			throw new NullPointerException("Object to marshal was null");
		}
		Marshaller m = null;

		StringWriter wr = null;
		try {

			m = JAXBPool.getInstance().borrowMarshaller(o.getClass());
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					   new Boolean(formatted));

			wr = new StringWriter();

			m.marshal(o, wr);

			FileUtils.write(file, stripNonValidXMLCharacters(wr.toString(), "&#160;"), "UTF-8");
		} finally {
			if (wr != null)
				wr.close();
			
			JAXBPool.getInstance().returnMarshaller(o.getClass(),m);
		}
	}
	/**
	 * Marshals the given object to an XML string
	 * @param o Object to serialize
	 * @return XML string
	 * @throws Exception
	 */
	public static String marshal(Object o) throws Exception {
		return marshal(o, false);
	}
	public static String marshal(Object o,boolean formatted) throws Exception {
		if (o==null) {
			throw new NullPointerException("Object to marshal was null");
		}
		Marshaller m = null;
		try {

			m = JAXBPool.getInstance().borrowMarshaller(o.getClass());
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					   new Boolean(formatted));

			StringWriter wr = new StringWriter();

			m.marshal(o, wr);

			return wr.toString();
		} finally {
			JAXBPool.getInstance().returnMarshaller(o.getClass(), m);
		}

	}


	/**
	 * This method ensures that the output String has only valid XML unicode characters as specified by the XML 1.0 standard. For reference, please see <a
	 * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the standard</a>. This method will return an empty String if the input is null or empty.
	 * 
	 * @param in The String whose non-valid characters we want to remove.
	 * @param repl Replacement for non-valid characters
	 * @return The in String, stripped of non-valid characters.
	 */
	public static String stripNonValidXMLCharacters(String in, String repl) {
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || ("".equals(in)))
			return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
			if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD)) || ((current >= 0x10000) && (current <= 0x10FFFF))) {
				out.append(current);
			} else {
				out.append(repl);
			}
		}
		return out.toString();
	}

	
}
