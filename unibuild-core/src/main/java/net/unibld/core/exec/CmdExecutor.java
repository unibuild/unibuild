package net.unibld.core.exec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.BuildTask;
import net.unibld.core.task.ExecutionResult;

/**
 * Executes an external command using Apache Commons Exec and exposes the 
 * output of the process via {@link ExecutionResult} and {@link IExecutionListener}.
 * @author andor
 *
 */
@Component
public class CmdExecutor {
	private static final Logger LOG = LoggerFactory
			.getLogger(CmdExecutor.class);
	/**
	 * Executes a command line using Commons Exec
	 * @param ctx Execution context
	 * @param line Command line to execute
	 * @param env Map of environment variables or null
	 * @param failOnError True if the build should fail on error, false if it should
	 * continue 
	 * @param ll Execution listener or null
	 * @return Execution result
	 * @throws IOException If the execution fails
	 */
	public <T extends BuildTask> ExecutionResult execCmd(CmdContext ctx, String line)
			throws IOException {
		if (ctx==null) {
			throw new IllegalArgumentException("CmdContext was null");
		}
		OutputStream outputStream = createOutputStream(ctx.getLogFilePath());

		CommandLine cmdLine = CommandLine.parse(line);

		LOG.info("Executing process: {}...", line);

		DefaultExecutor executor = new DefaultExecutor();
		if (ctx.getWorkingDir()!=null) {
			executor.setWorkingDirectory(ctx.getWorkingDir());
			LOG.info("Executing process: {} in {}...", line,ctx.getWorkingDir().getAbsolutePath());
		} else {
			LOG.info("Executing process: {}...", line);
		}
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		executor.setStreamHandler(streamHandler);

		int exitValue;
		if (ctx.getEnv() != null) {
			Map<String, String> env = mergeEnvVars(ctx);
		    
			exitValue = executor.execute(cmdLine, env);
		} else {
			exitValue = executor.execute(cmdLine);
		}
		String output = outputStream instanceof ByteArrayOutputStream?fromStream((ByteArrayOutputStream)outputStream):null;

		ctx.setOutput(output);
		
		if (ctx.getListener()!=null&&output!=null) {
			ctx.getListener().logExecution(line,output);
			ctx.getListener().handleOutput(output);
		}
		
		if (output!=null) {
			LOG.info("Output: {}", output);
		}
		

		if (exitValue == 0) {
			LOG.info("Successfully executed process {}", line);
			return ExecutionResult.buildSuccess();
		} else {
			LOG.warn(
					"Failed to execute process {}, exit value: {}", line,
					exitValue);
			if (ctx.isFailOnError()) {
				return ExecutionResult.buildError(String.format(
						"Error code: %d", exitValue));
			} else {
				return ExecutionResult.buildSuccess();
			}
		}
	}

	private Map<String, String> mergeEnvVars(CmdContext ctx) {
		Map<String,String> env=new HashMap<>();
		Map<String, String> origEnv = System.getenv();
		for (Map.Entry<String, String> e : origEnv.entrySet()) {
			env.put(e.getKey(), e.getValue());
		}
		
		for (Object envKey : ctx.getEnv().keySet()) {
			String envName=(String) envKey;
			env.put(envName, ((String)ctx.getEnv().get(envName)));
		}
		return env;
	}

	/**
	 * Creates an {@link OutputStream} for the output of the process being executed:
	 * if logFilePath is specified a {@link FileOutputStream} is created, otherwise
	 * a {@link ByteArrayOutputStream} is created.
	 * @param logFilePath Log file path for the execution or null
	 * @return Output stream to write the output of the child process
	 * @throws FileNotFoundException If an invalid log file path was specified
	 */
	public OutputStream createOutputStream(String logFilePath) throws FileNotFoundException {
		if (logFilePath!=null) {
			File logFile = new File(logFilePath);
			if (logFile.getParentFile()!=null) {
				logFile.getParentFile().mkdirs();
			}
			return new FileOutputStream(logFile); 
				
		} else {
			return new ByteArrayOutputStream();
		}
	}

	protected String fromStream(ByteArrayOutputStream outputStream) {
		return outputStream.toString();
	}

	

}
