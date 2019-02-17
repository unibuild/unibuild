package net.unibld.core.task.impl.util;

import java.io.File;

import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.Zip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Executes a {@link ZipTask} that can create zip archives from a directory or from a list of files.
 * @author andor
 *
 */
@Component
public class ZipTaskRunner extends BaseTaskRunner<ZipTask> {
	private static final Logger LOG=LoggerFactory.getLogger(ZipTaskRunner.class);
	
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "zip";
	}

	@Override
	protected ExecutionResult execute(ZipTask task) {
		
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		if (task.getOutput()==null||task.getOutput().trim().length()==0) {
			throw new IllegalStateException("Output was null or empty");
		}
		if (isDirEmpty(task)&&isFilesEmpty(task)) {
			throw new IllegalStateException("Both files and dir were null or empty");
		}
		if (!isDirEmpty(task)&&!isFilesEmpty(task)) {
			throw new IllegalStateException("Both files and dir were specified");
		}
		
		
		try {
			File outputFile=new File(task.getOutput());
			if (outputFile.getParentFile()!=null) {
				outputFile.getParentFile().mkdirs();
			}
			if (task.getDir()!=null) {
				logTask("Zipping directory {0} to: {1}...", task.getDir(),task.getOutput());
				Zip.zip(task.getDir(),task.getOutput());
			} else {
				String[] files = task.getFiles().split(",");
				if (files.length==0) {
					throw new IllegalArgumentException("File count was 0");
				}
				logTask("Zipping {0} files to: {1}...", String.valueOf(files.length),task.getOutput());

				File[] farr=new File[files.length];
				int i=0;
				for (String fpath:files) {
					farr[i]=new File(fpath);
					i++;
				}
				
				Zip.zip(farr, task.getOutput(), null);
			}
			
			return ExecutionResult.buildSuccess();
			
		} catch (Exception ex) {
			logError("Failed to execute zip task",ex);
			return ExecutionResult.buildError("Failed to execute zip task", ex);
		}
	}

	protected boolean isFilesEmpty(ZipTask task) {
		return task.getFiles()==null||task.getFiles().trim().length()==0;
	}

	protected boolean isDirEmpty(ZipTask task) {
		return task.getDir()==null||task.getDir().trim().length()==0;
	}
}
