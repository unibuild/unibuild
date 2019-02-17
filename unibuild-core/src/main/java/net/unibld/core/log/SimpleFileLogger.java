package net.unibld.core.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.unibld.core.BuildProject;
import net.unibld.core.build.BuildConstants;
import net.unibld.core.build.TaskResult;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the {@link IBuildLogger} interface that produce build logs to a file,
 * with the exception of debug level messages
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:34
 */
public class SimpleFileLogger extends AbstractLogger implements IBuildLogger {
	private static final Logger LOG = LoggerFactory
			.getLogger(SimpleFileLogger.class);
	private static final String DEFAULT_FILENAME_DATE_FORMAT = "yyyyMMdd.HHmmss";

	private String fileName;
	private Date fileNameDate;
	private String logDir;

	private String fileNameDateFormat;

	private String filePath;
	
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void logDebug(String msg, String... args) {
		if (isVerbose()) {
			logToFile(getFilePath(),
					"DEBUG:" + LogFormatter.replaceArgs(msg, args));
		}
	}

	protected static DateFormat DF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	protected static void logToFile(String logFilePath, String log) {
		if (logFilePath == null) {
			throw new IllegalArgumentException(
					"Log file path was not specified");
		}
		File fi = new File(logFilePath);
		if (!fi.getParentFile().exists()) {
			fi.getParentFile().mkdirs();
		}

		String msg = String.format("%s   %s\r\n", DF.format(new Date()), log);

		try {
			appendBytesToFile(logFilePath, msg.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 is not supported", e);
		}
	}

	private static void appendBytesToFile(String path, byte[] bytes) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path, true);
			fos.write(bytes);
		} catch (FileNotFoundException ex) {
			LOG.error("File not found: " + path, ex);
		} catch (IOException ioe) {
			LOG.error("File I/O error: " + path, ioe);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOG.error("Failed to close output stream", e);
				}
			}

		}

	}

	@Override
	public void logTask(String taskName, String msg, String... args) {

		logToFile(getFilePath(), LogFormatter.replaceArgs(msg, args));
	}

	@Override
	public void logTaskResult(String taskName, TaskResult result) {
		logToFile(getFilePath(),
				String.format("%s result: %s", taskName, result.name()));
	}

	@Override
	public void logTaskResult(String taskName, String result) {
		logToFile(getFilePath(),
				String.format("%s result: %s", taskName, result));
	}

	@Override
	public void logError(String msg, Throwable ex, String... args) {
		logToFile(getFilePath(),
				"ERROR: " + LogFormatter.replaceArgs(msg, args));
		if (ex != null) {
			logToFile(getFilePath(), ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	public void logError(String msg, String... args) {
		logToFile(getFilePath(),
				"ERROR: " + LogFormatter.replaceArgs(msg, args));
	}

	public String getFilePath() {
		if (filePath != null) {
			return filePath;
		}
		String var = String.format("{%s}",
				BuildConstants.VARIABLE_NAME_BUILD_DATE);
		if (fileName != null && !fileName.contains(var)) {
			if (logDir == null) {
				filePath = new File(fileName).getAbsolutePath();
			} else {
				filePath = new File(FilenameUtils.concat(logDir, fileName))
						.getAbsolutePath();
			}

		} else if (fileName != null && fileNameDate != null) {
			String fn = fileName.replace(var, formatFileNameDate(fileNameDate));
			if (logDir == null) {
				filePath = new File(fn).getAbsolutePath();
			} else {
				filePath = new File(FilenameUtils.concat(logDir, fn))
						.getAbsolutePath();
			}

		} else if (fileName == null && fileNameDate != null) {
			String fn = String.format("build.%s.log",
					formatFileNameDate(fileNameDate));
			if (logDir == null) {
				filePath = new File(fn).getAbsolutePath();
			} else {
				filePath = new File(FilenameUtils.concat(logDir, fn))
						.getAbsolutePath();
			}

		} else if (fileName == null && fileNameDate == null) {
			if (logDir == null) {
				filePath = new File("build.log").getAbsolutePath();
			} else {
				filePath = new File(FilenameUtils.concat(logDir, "build.log"))
						.getAbsolutePath();
			}
		}

		if (filePath != null) {
			LOG.info("Log file path setup: {}", filePath);
			return filePath;
		}
		throw new IllegalArgumentException(
				String.format(
						"Illegal configuration for SimpleFileLogger: {logDir=%s,fileName=%s,fileNameDate=%s,fileNameDateFormat=%s}",
						logDir, fileName, fileNameDate, fileNameDateFormat));
	}

	private String formatFileNameDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Date was null");
		}
		if (fileNameDateFormat != null) {
			SimpleDateFormat df = new SimpleDateFormat(fileNameDateFormat);
			return df.format(date);
		} else {
			SimpleDateFormat df = new SimpleDateFormat(
					DEFAULT_FILENAME_DATE_FORMAT);
			return df.format(date);
		}
	}

	public Date getFileNameDate() {
		return fileNameDate;
	}

	public void setFileNameDate(Date fileNameDate) {
		this.fileNameDate = fileNameDate;
	}

	public String getFileNameDateFormat() {
		return fileNameDateFormat;
	}

	public void setFileNameDateFormat(String fileNameDateFormat) {
		this.fileNameDateFormat = fileNameDateFormat;
	}

	public String getFileName() {
		return fileName;
	}

	public String getLogDir() {
		return logDir;
	}

	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}


	@Override
	public void welcome(BuildProject project,String goal, String logFile) {
		// do nothing

	}

	@Override
	public void logTaskFailureReason(String taskName, String failureReason) {
		logToFile(getFilePath(), String.format("%s failure reason: %s",
				taskName, failureReason != null ? failureReason : "N/A"));
	}

	@Override
	public void logTaskErrorLogPath(String taskName, String failureReason) {
		// do nothing
	}



}// end SimpleFileLogger