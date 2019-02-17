package net.unibld.core.log;

/**
 * An implementation of the {@link IBuildLogger} interface that produce build detailed logs to a file,
 * including of debug level messages
 * @author andor
 *
 */
public class DetailedFileLogger extends SimpleFileLogger {

	@Override
	public void logDebug(String msg, String... args) {
		logToFile(getFilePath(),"DEBUG: "+LogFormatter.replaceArgs(msg,args));
	}

}
