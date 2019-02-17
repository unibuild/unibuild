package net.unibld.core.log;

import net.unibld.core.BuildProject;
import net.unibld.core.build.TaskResult;


/**
 * An implementation of the {@link IBuildLogger} interface that produce build logs to the console,
 * with the exception of debug level messages
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:33
 */
public class SimpleConsoleLogger extends AbstractLogger implements IBuildLogger {

	private static final int DEFAULT_LINE_LENGTH_CHARS = 100;
	private int lineLengthChars=DEFAULT_LINE_LENGTH_CHARS;
	
	
	@Override
	public void logDebug(String msg, String... args) {
		// nothing
		
	}

	@Override
	public void logTask(String taskName, String msg, String... args) {
		String str = LogFormatter.replaceArgs(msg,args);
		int dots=lineLengthChars-str.length()-1;
		/*if (dots<=0) {
			System.out.print(str);
		} else {
			StringBuilder b=new StringBuilder();
			b.append(str);
			b.append(" ");
			for (int i=0;i<dots;i++) {
				b.append('.');
			}
			System.out.print(b.toString());
		}*/
		System.out.print(str);
	}

	@Override
	public void logTaskResult(String taskName, TaskResult result) {
		System.out.println(String.format(" %s", result.name()));
	}

	@Override
	public void logTaskResult(String taskName, String result) {
		System.out.println(String.format(" %s", result));
	}
	@Override
	public void logError(String msg, Throwable ex, String... args) {
		if (isVerbose()) {
			System.out.println("ERROR: "+LogFormatter.replaceArgs(msg,args));
			if (ex!=null) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void logError(String msg, String... args) {
		if (isVerbose()) {
			System.out.println("ERROR: "+LogFormatter.replaceArgs(msg,args));
		}
	}

	

	@Override
	public void welcome(BuildProject project,String goal,String logFile) {
		System.out.println(WelcomeMessageHelper.getWelcomeMessage(project,goal,logFile));
	}

	@Override
	public void logTaskFailureReason(String taskName, String failureReason) {
		System.out.println(String.format("%s failure reason: %s", taskName,failureReason!=null?failureReason:"N/A"));
	}
	
	@Override
	public void logTaskErrorLogPath(String taskName, String logFilePath) {
		System.out.println(String.format("%s error log: %s", taskName,logFilePath!=null?logFilePath:"N/A"));
	}



}//end SimpleConsoleLogger