package net.unibld.core.util.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.pool.BasePoolableObjectFactory;

/**
 * A factory class for pooled JAXB marshallers
 * @author andor
 *
 */
public class JAXBMarshallerFactory extends BasePoolableObjectFactory {
	private Class<?> contextClass;

	/**
	 * @param contextClass JAXB context class
	 */
	public JAXBMarshallerFactory(Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	@Override
	public Object makeObject() throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(contextClass);
		Marshaller m = ctx.createMarshaller();
		//m.setProperty("com.sun.xml.bind.namespacePrefixMapper",new SLNamespacePrefixMapper());

		return m;

	}

}
