package net.unibld.core.task.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.PlatformHelper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Runs OS shell scripts Apache Commons Exec.
 * @author andor
 * @version 1.0
 * @param <T> Generic task type that should extend {@link ExecTask}
 * @created 21-05-2013 17:16:29
 */
public abstract class ShellTaskRunner<T extends ExecTask> extends BaseTaskRunner<T> {
	private static final Logger LOG=LoggerFactory.getLogger(ShellTaskRunner.class);
	
	@Autowired
	protected CmdExecutor exec;

	@SuppressWarnings("rawtypes")
	protected Map getEnvironment(T task) {
		return null;
	}
	protected ExecutionResult execute(T task) {
		validate(task);
		
		String args = task.getArgs();
		
		CmdContext ctx=new CmdContext();
		ctx.setFailOnError(true);
		
		try {
			String cmd = createCmd(task, args);
			LOG.info("Executing shell command: {}...",cmd);
			try {
				return exec.execCmd(ctx,cmd);
			} catch (Exception ex) {
				LOG.error("Failed to exec shell command",ex);
				return ExecutionResult.buildError("Failed to exec shell command",ex);
			}
			
		} catch (Exception ex) {
			String msg = String.format("Failed to execute process %s",task.getPath());
			LOG.error(msg,ex);
			if (ctx.isFailOnError()) {
				return ExecutionResult.buildError(msg, ex);
			} else {
				return ExecutionResult.buildSuccess();
			}
		}
	}

	
	@SuppressWarnings("unchecked")
	private String createCmd(T task, String args) {
		String path = task.getPath();
		String cmd=(args!=null&&!StringUtils.isEmpty(args))?String.format("%s %s",path,args):path;
		Map<String,String> env = getEnvironment(task);
		if (env==null||env.isEmpty()) {
			return cmd;
		}
		if (PlatformHelper.isUnix()) {
			//appending sh prefix
			cmd="sh "+cmd;
		} 
		return cmd;
	}

	

	protected String getArguments(T task) {
		return task.getArgs();
	}

	
	

	protected void validate(T task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
	}

	

	@Override
	protected Logger getLogger() {
		return LOG;
	}


	
	
	
}//end ShellTaskRunner