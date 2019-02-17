package net.unibld.core.task.impl.sys;

import net.unibld.core.task.annotations.Task;



/**
 * A task that uses SCP for file transfers
 * @author andor
 *
 */
@Task(name="scp",runnerClass=ScpTaskRunner.class)
public class ScpTask extends AbstractSshTask {

	
	private static final long serialVersionUID = -952772513109827227L;

	private String file;
	private String remoteDir;
	
	private String remoteFile;
	private String remoteFileName;
	private String localDir;
	
	
	/**
	 * Default constructor
	 */
	public ScpTask(){

	}

	/**
	 * @return File to send, if null the operation is considered a download
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file File to send, if null the operation is considered a download
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return Remote dir to upload to
	 */
	public String getRemoteDir() {
		return remoteDir;
	}

	/**
	 * @param remoteDir Remote dir to upload to
	 */
	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	/**
	 * @return Remote file path when downloading
	 */
	public String getRemoteFile() {
		return remoteFile;
	}

	/**
	 * @param remoteFile Remote file path when downloading
	 */
	public void setRemoteFile(String remoteFile) {
		this.remoteFile = remoteFile;
	}

	/**
	 * @return The local directory to download a file to
	 */
	public String getLocalDir() {
		return localDir;
	}

	/**
	 * @param localDir The local directory to download a file to
	 */
	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	/**
	 * @return Remote file name or null for keeping original file name when uploading (remoteDir is
	 * required in this case)
	 */
	public String getRemoteFileName() {
		return remoteFileName;
	}

	/**
	 * @param remoteFileName Remote file name or null for keeping original file name when uploading (remoteDir is
	 * required in this case)
	 */
	public void setRemoteFileName(String remoteFileName) {
		this.remoteFileName = remoteFileName;
	}

	
	
}