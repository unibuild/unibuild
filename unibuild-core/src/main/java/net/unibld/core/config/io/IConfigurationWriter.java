package net.unibld.core.config.io;

import java.io.OutputStream;

/**
 * A generic interface for writing a configuration class.
 * @author andor
 * @version 1.0
 * @param <T> Generic configuration class
 * @updated 22-05-2013 3:47:24
 */
public interface IConfigurationWriter<T> {

	/**
	 * 
	 * @param out
	 * @param config
	 * @throws Exception 
	 */
	public void writeConfig(OutputStream out, T config) throws Exception;

}