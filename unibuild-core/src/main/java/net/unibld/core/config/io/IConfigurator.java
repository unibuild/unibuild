package net.unibld.core.config.io;


/**
 * A generic interface that declares load and save methods for a specific configuration class using a
 * format specified by the implementor.
 * @author andor
 * @version 1.0
 * @param <T> Generic configuration type
 * @updated 22-05-2013 3:46:59
 */
public interface IConfigurator<T> extends IConfigurationWriter<T>, IConfigurationReader<T> {


	
}//end AbstractConfigurator