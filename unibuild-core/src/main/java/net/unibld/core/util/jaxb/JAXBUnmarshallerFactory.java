package net.unibld.core.util.jaxb;

import javax.xml.bind.JAXBContext;

import org.apache.commons.pool.BasePoolableObjectFactory;

/**
 * A factory class for pooled JAXB marshallers
 * @author andor
 *
 */
public class JAXBUnmarshallerFactory extends BasePoolableObjectFactory {
	private Class<?> contextClass;
	
	/**
	 * @param contextClass JAXB context class
	 */
	public JAXBUnmarshallerFactory(Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	@Override
	public Object makeObject() throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(contextClass);
		return ctx.createUnmarshaller();

	}

}
