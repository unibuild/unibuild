package net.unibld.core.task.impl.sys;

import java.io.File;

import net.unibld.core.build.BuildException;
import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.lib.FileState;
import net.unibld.core.lib.LinuxFileHelper;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.PlatformHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Performs a chmod command on the specified file or directory. Only available on
 * Unix-based systems.
 * @author andor
 *
 */
@Component
public class ChmodTaskRunner extends BaseTaskRunner<ChmodTask> {
	
	private static final Logger LOG=LoggerFactory.getLogger(ChmodTaskRunner.class);
	
	@Autowired
	private CmdExecutor cmdExecutor;

	protected ExecutionResult execute(ChmodTask task) {
		if (PlatformHelper.isWindows()) {
			throw new BuildException("Chmod is not available on Windows");
		}
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		
		
		
		
		if (task.getPath()==null) {
			throw new BuildException("Path was not specified");
		}
		if (task.getMode()==null) {
			throw new BuildException("Mode was not specified");
		}
		
		logTask(String.format("Executing chmod %s on %s...",task.getMode(),task.getPath()));

		try {
			Integer.parseInt(task.getMode());
		} catch (NumberFormatException e) {
			throw new BuildException("Invalid mode: "+task.getMode());
		}
		
		
		FileState fileState=LinuxFileHelper.getFileState(cmdExecutor,task.getPath());
		File file=new File(task.getPath());
		if (fileState==FileState.NOT_EXISTS) {
			throw new BuildException("Path does not exist: "+file.getAbsolutePath());
		}
		
		
		CmdContext ctx=new CmdContext();
		ctx.setFailOnError(true);
		if (fileState==FileState.DIR&&task.isRecursive()) {
			//recurive chmod for dirs
			LOG.info("Invoking recursive chmod for dir: {}...",file.getAbsolutePath());
			try {
				return cmdExecutor.execCmd(ctx,String.format("chmod -R %s %s",task.getMode(),file.getAbsolutePath()));
			} catch (Exception ex) {
				LOG.error("Failed to exec recursive chmod",ex);
				return ExecutionResult.buildError("Failed to exec recursive chmod",ex);
			}
		} else {
			LOG.info("Invoking native chmod for {}: {}...",fileState==FileState.DIR?"dir":"file",file.getAbsolutePath());
			try {
				return cmdExecutor.execCmd(ctx,String.format("chmod %s %s",task.getMode(),file.getAbsolutePath()));
			} catch (Exception ex) {
				LOG.error("Failed to exec chmod",ex);
				return ExecutionResult.buildError("Failed to exec chmod",ex);
			}
			
		}
	}



	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "chmod";
	}
}//end CustomNativeTaskRunner