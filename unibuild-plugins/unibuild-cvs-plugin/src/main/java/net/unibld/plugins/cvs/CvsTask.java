package net.unibld.plugins.cvs;

import net.unibld.core.BuildTask;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.task.annotations.Task;

/**
 * A task that performs a CVS operation.<br>
 * CVS is generally considered obsolete compared to git and SVN so we do not really intend to maintain this task.
 * @author andor
 *
 */
@Task(name="cvs",runnerClass=CvsTaskRunner.class)
public class CvsTask extends BuildTask {
	
	private static final long serialVersionUID = -4639390917713636293L;
	
	public static final String CONNECTION_TYPE_PSERVER = "pserver";
	public static final String CONNECTION_TYPE_EXT = "ext";
	public static final String CONNECTION_TYPE_LOCAL = "local";

	private String cvsRoot;
	
	private String command;
	private String hostName;
	private String userName;
	private String repositoryPath;
	private PasswordStrategy passwordStrategy=PasswordStrategy.stored;

	
	private String checkoutDir;
	
	private String checkoutRelativePath;
	private String exportDir;
	private String connectionType;
	private String module;
	private String branch;
	
	private String encodedPassword;
	
	private int port=22;
	
	public String getCvsRoot() {
		return cvsRoot;
	}
	public void setCvsRoot(String cvsRoot) {
		this.cvsRoot = cvsRoot;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
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
	public PasswordStrategy getPasswordStrategy() {
		return passwordStrategy;
	}
	public void setPasswordStrategy(PasswordStrategy passwordStrategy) {
		this.passwordStrategy = passwordStrategy;
	}
	public String getCheckoutRelativePath() {
		return checkoutRelativePath;
	}
	public void setCheckoutRelativePath(String checkoutDir) {
		this.checkoutRelativePath = checkoutDir;
	}
	public String getExportDir() {
		return exportDir;
	}
	public void setExportDir(String exportDir) {
		this.exportDir = exportDir;
	}
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getEncodedPassword() {
		return encodedPassword;
	}
	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getCheckoutDir() {
		return checkoutDir;
	}
	public void setCheckoutDir(String checkoutDir) {
		this.checkoutDir = checkoutDir;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
}
