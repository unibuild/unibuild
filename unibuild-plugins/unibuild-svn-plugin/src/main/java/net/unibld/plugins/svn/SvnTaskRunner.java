package net.unibld.plugins.svn;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.AbstractExecTaskRunner;

/**
 * A task that executes an SVN command using native SVN support on the local 
 * platform
 * @author andor
 *
 */
@Component
public class SvnTaskRunner extends AbstractExecTaskRunner<SvnTask> {
	private static final Logger LOG=LoggerFactory.getLogger(SvnTaskRunner.class);

	@Autowired
	private SvnSupport svnSupport;
	@Autowired
	private CmdExecutor cmdExecutor;
	
	private String builtInStorePath;	
	
	@Override
	protected String getArguments(SvnTask task) {
		SvnCommand command=(SvnCommand) task.getTaskConfig().getTaskContext().getSerializable("svn.command");
		if (command==null) {
			throw new IllegalStateException("SVN command not found");
		}
		return command.getArguments();
	}
	protected boolean isExport(SvnTask task) {
		return "export".equals(task.getCommand());
	}
	protected boolean isCommit(SvnTask task) {
		return "commit".equals(task.getCommand());
	}
	protected boolean isUpdate(SvnTask task) {
		return "update".equals(task.getCommand());
	}
	
	protected boolean isCheckout(SvnTask task) {
		return "checkout".equals(task.getCommand());
	}


	
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "svn";
	}
	
	@Override
	protected ExecutionResult execute(SvnTask task) {
		svnSupport.setBuiltInStorePath(builtInStorePath);
		
	
		
		Class<? extends SvnCommand> klazz=null;
		
		if (isCheckout(task)) {
			logTask("Checking out from SVN: {0}...", task.getRepositoryUrl()!=null?task.getRepositoryUrl():task.getRepositoryPath());
			klazz=SvnCheckout.class;
		} else if (isExport(task)) {
			logTask("Exporting from SVN checkout to: {0}...", task.getExportDir());
			klazz=SvnExport.class;
		} else if (isUpdate(task)) {
			logTask("Executing SVN update...");
			klazz=SvnUpdate.class;
		} else if (isCommit(task)) {
			logTask("Executing SVN commit...");
			klazz=SvnCommit.class;
		} else {
			throw new BuildException("Unsupported SVN command: "+task.getCommand());
		}

		SvnCommand svnCommand=null;

		try {
			svnCommand=svnSupport.create(klazz);
		} catch (Exception e) {
			LOG.error("Failed to create SVN command",e);
			throw new BuildException("Failed to create SVN command",e);
		}
		
		String buildDir = getBuildDirPath(task);
		
		String origPath=new File(buildDir).getAbsolutePath();
		svnCommand.setLogFilePath(getLogFilePath(task));
		svnCommand.setOrigPath(origPath);
		svnCommand.setBuildDir(buildDir);
		svnCommand.setFailOnError(isFailOnError(task));
		svnCommand.setExecutor(cmdExecutor);
		if (SvnExport.class.isAssignableFrom(svnCommand.getClass())) {
			((SvnExport)svnCommand).setRecursiveExportEnabled(task.isRecursiveExportEnabled());
		}
		
		TaskAttributeContainer container = createAttributeContainer(task);
		
		svnCommand.prepare(container,task);
		
		svnCommand.setCredential(svnSupport.getPasswordForTask(container,task));
		task.getTaskConfig().getTaskContext().addSerializableAttribute("svn.command", svnCommand);
		
		ExecutionResult ret = svnSupport.execute(svnCommand);
		
		svnCommand.postExecute(ret);
		
		return ret;
	}
	
	/**
	 * @return The directory to read SVN auth info from (useful for unit testing)
	 */
	public String getBuiltInStorePath() {
		return builtInStorePath;
	}
	/**
	 * @param builtInStorePath The directory to read SVN auth info from (useful for unit testing)
	 */
	public void setBuiltInStorePath(String builtInStorePath) {
		this.builtInStorePath = builtInStorePath;
	}
	
	
}
