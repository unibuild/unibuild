package net.unibld.core.task.impl.sys;

import java.io.File;

import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * A task that copies a file or a directory to a specified destination file or directory.
 * @author andor
 *
 */
@Component
public class CpTaskRunner extends BaseTaskRunner<CpTask> {
	private static final Logger LOG=LoggerFactory.getLogger(CpTaskRunner.class);
	


	protected ExecutionResult execute(CpTask task) {
		
		
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		if (task.getSource()==null) {
			throw new IllegalArgumentException("Source was not specified");
		}
		if (task.getTarget()==null&&task.getTargetFile()==null) {
			throw new IllegalArgumentException("Target or targetFile was not specified");
		}
		if (task.getTarget()!=null) {
			logTask("Copying {0} to: {1}", task.getSource(),task.getTarget());
		} else if (task.getTargetFile()!=null) {
			logTask("Copying {0} to file: {1}", task.getSource(),task.getTargetFile());
		}
		
		File sourceFile=new File(task.getSource());
		
		if (!sourceFile.exists()) {
			return ExecutionResult.buildError("Source file does not exist: "+task.getSource());
		}
		if (!sourceFile.isFile()&&task.getTarget()==null) {
			return ExecutionResult.buildError("Target dir must be specified if source is a directory: "+task.getSource());
		}
		
		File targetDir=null;
		File targetFile=null;
		if (task.getTarget()!=null) {
			targetDir=new File(task.getTarget());
			targetFile=new File(FilenameUtils.concat(targetDir.getAbsolutePath(), sourceFile.getName()));
		} else {
			targetFile=new File(task.getTargetFile());
			targetDir=targetFile.getParentFile();
		}
		
		if (!task.isOverwrite()) {
			//String check = FilenameUtils.concat(targetFile.getAbsolutePath(), sourceFile.getName());
			if (targetFile.exists()) {
				LOG.info("Target already exists: {}",task.getTarget());
				
				return ExecutionResult.buildError("Target already exists: "+targetFile.getAbsolutePath());
			}
		}
		
		if (targetDir!=null&&targetDir.exists()&&targetDir.isFile()) {
			return ExecutionResult.buildError("Target already exists as a file: "+task.getTarget());
		}
		
		try {
			if (targetDir!=null) {
				targetDir.mkdirs();
			}
			if (sourceFile.isFile()) {
				LOG.debug("Copying source file {} to target dir: {}...",sourceFile.getAbsolutePath(),targetDir.getAbsolutePath());
				FileUtils.copyFile(sourceFile, targetFile);
				LOG.info("Copied source file {} to target dir: {}...",sourceFile.getAbsolutePath(),targetDir.getAbsolutePath());
			} else {
				LOG.debug("Copying source dir {} to target dir: {}...",sourceFile.getAbsolutePath(),targetDir.getAbsolutePath());
				FileUtils.copyDirectory(sourceFile, targetDir);
				LOG.info("Copied source dir {} to target dir: {}...",sourceFile.getAbsolutePath(),targetDir.getAbsolutePath());
			
			}
			
			
			return ExecutionResult.buildSuccess();
		} catch (Exception ex) {
			String msg = String.format("Failed to copy source %s to target: %s",sourceFile.getAbsolutePath(),targetDir.getAbsolutePath());
			LOG.error(msg,ex);
			return ExecutionResult.buildError(msg,ex);
		}
	}


	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "cp";
	}
}//end CustomNativeTaskRunner