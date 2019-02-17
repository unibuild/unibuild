package net.unibld.core.config.io;

import java.io.InputStream;

import net.unibld.core.build.IBuildContextAttributeContainer;

/**
 * A generic interface for reading a configuration class.
 * @author andor
 * @version 1.0
 * @param <T> Generic configuration type
 * @updated 22-05-2013 3:47:24
 */
public interface IConfigurationReader<T> {

	/**
	 * Reads the content of an {@link InputStream} into a new instance of
	 * the specified configuration class
	 * @param context Context
	 * @param klazz Configuration class to instantiate
	 * @param is Input stream to read
	 * @return Configuration object created
	 * @throws Exception If any error occurs
	 */
	public T readConfig(IBuildContextAttributeContainer context,Class<T> klazz,InputStream is) throws Exception;

}