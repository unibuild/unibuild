package net.unibld.core.config.io;

import java.io.InputStream;
import java.io.OutputStream;

import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.util.jaxb.JAXBUtil;

/**
 * A generic class that loads and saves a specific configuration class from/to XML
 * format using JAXB.
 * @author andor
 * @version 1.0
 * @param <T> Generic configuration type
 * @updated 22-05-2013 3:47:37
 */
public class JaxbConfigurator<T> implements IConfigurator<T> {

	@SuppressWarnings("unchecked")
	public T readConfig(IBuildContextAttributeContainer context,Class<T> klazz,InputStream is) throws Exception{
		return (T) JAXBUtil.unmarshalFromStream(klazz, is);
	}

	
	public void writeConfig(OutputStream out, T config) throws Exception{
		JAXBUtil.marshalToStream(config, out, true);
	}
}//end XmlConfigurator