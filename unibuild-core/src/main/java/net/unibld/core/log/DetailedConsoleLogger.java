package net.unibld.core.log;

/**
 * An implementation of the {@link IBuildLogger} interface that produce build detailed logs to the console,
 * including of debug level messages
 * @author andor
 *
 */
public class DetailedConsoleLogger extends SimpleConsoleLogger {

	@Override
	public void logDebug(String msg, String... args) {
		StringBuilder b=new StringBuilder();
		for (String arg:args) {
			b.append(" ");
			b.append(arg);
		}
		System.out.println(String.format("DEBUG: %s%s", msg,b.toString()));
	}
	
	@Override
	public void logError(String msg, Throwable ex, String... args) {
		System.out.println("ERROR: "+LogFormatter.replaceArgs(msg,args));
		if (ex!=null) {
			ex.printStackTrace();
		}
	}
	@Override
	public void logError(String msg, String... args) {
		System.out.println("ERROR: "+LogFormatter.replaceArgs(msg,args));
	}
}
