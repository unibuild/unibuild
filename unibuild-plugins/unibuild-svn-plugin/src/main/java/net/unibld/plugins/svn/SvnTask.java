package net.unibld.plugins.svn;

import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ExecTask;
import net.unibld.core.util.PlatformHelper;

@Task(name="svn",runnerClass=SvnTaskRunner.class)
public class SvnTask extends ExecTask{
	private String command;
	private String repositoryUrl;
	private String userName;
	private String repositoryPath;
	private PasswordStrategy passwordStrategy=PasswordStrategy.stored;

	private String checkoutDir;
	private String exportDir;
	private String comment;

	private String force;
	private String useBuiltinStore;
	private boolean recursiveExportEnabled=false;

	public PasswordStrategy getPasswordStrategy() {
		return passwordStrategy;
	}





	public void setPasswordStrategy(PasswordStrategy passwordStrategy) {
		this.passwordStrategy = passwordStrategy;
	}





	





	public String getCommand() {
		return command;
	}


	public void setCommand(String command) {
		this.command = command;
	}





	


	
	
	public String getRepositoryUrl() {
		return repositoryUrl;
	}


	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	

	public String getRepositoryPath() {
		return repositoryPath;
	}


	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}


	@Override
	public String getPath() {
		if (PlatformHelper.isWindows()) {
			return "svn.exe";
		} else {
			return "svn";
		}
		
	}





	public String getCheckoutDir() {
		return checkoutDir;
	}





	public void setCheckoutDir(String checkoutDir) {
		this.checkoutDir = checkoutDir;
	}





	public String getExportDir() {
		return exportDir;
	}





	public void setExportDir(String exportDir) {
		this.exportDir = exportDir;
	}





	public String getComment() {
		return comment;
	}





	public void setComment(String comment) {
		this.comment = comment;
	}





	public String getForce() {
		return force;
	}





	public void setForce(String force) {
		this.force = force;
	}

	public boolean isForcing() {
		return "true".equals(force);
	}





	public boolean isUseBuiltinStore() {
		return useBuiltinStore==null||"true".equals(useBuiltinStore);
	}





	public String getUseBuiltinStore() {
		return useBuiltinStore;
	}





	public void setUseBuiltinStore(String useBuiltinStore) {
		this.useBuiltinStore = useBuiltinStore;
	}





	public boolean isRecursiveExportEnabled() {
		return recursiveExportEnabled;
	}





	public void setRecursiveExportEnabled(boolean recursiveExportEnabled) {
		this.recursiveExportEnabled = recursiveExportEnabled;
	}
}
