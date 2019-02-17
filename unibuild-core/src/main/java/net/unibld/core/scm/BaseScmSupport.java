package net.unibld.core.scm;

import java.io.File;
import java.io.IOException;

import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.exec.IExecutionListener;
import net.unibld.core.task.ExecutionResult;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseScmSupport<C extends BaseScmCommand<?>> implements IScmSupport<C> {
	protected class ScmSupportExecutionListener implements IExecutionListener {
		private C cmd;

		protected ScmSupportExecutionListener(C cmd) {
			this.cmd=cmd;
		}
		@Override
		public void logExecution(String line,String output) {
			
			if (cmd != null&&cmd.getLogFilePath()!=null) {
				File file = new File(cmd.getLogFilePath());
				file.getParentFile().mkdirs();
				try {
					FileUtils.writeStringToFile(file, output, "UTF-8");
				} catch (IOException e) {
					LoggerFactory.getLogger(getClass()).error("Failed to write output to file",e);
				}

			} else {
				LoggerFactory.getLogger(getClass()).warn("No logging for command execution");
			}
		}

		@Override
		public void handleOutput(String output) {
			// do nothing
			
		}
		
	}
	@Autowired
	protected CmdExecutor cmdExecutor;

	public ExecutionResult execute(C cmd) {
		String args = cmd.getArguments();
		String line=null;
		if (args!=null) {
			line = String.format("%s %s",cmd.getExecutable() , args);
		} else {
			line = cmd.getExecutable();
		}
		
		ScmSupportExecutionListener l=new ScmSupportExecutionListener(cmd);
		CmdContext ctx=new CmdContext();
		ctx.setListener(l);
		ctx.setFailOnError(cmd.isFailOnError());
		if (cmd.getWorkingDirectory()!=null) {
			ctx.setWorkingDir(cmd.getWorkingDirectory());
		}
		ctx.setLogFilePath(cmd.getLogFilePath());
		ctx.setEnv(cmd.getEnvironment());
		
		try {
			return cmdExecutor.execCmd(ctx,line);
		} catch (Exception ex) {
			String msg = String.format("Failed to execute process %s",cmd.getExecutable());
			if (cmd.isFailOnError()) {
				
				return ExecutionResult.buildError(msg, ex,cmd.getLogFilePath());
			} else {
				return ExecutionResult.buildSuccess();
			}
		}
	}
	
	
}
