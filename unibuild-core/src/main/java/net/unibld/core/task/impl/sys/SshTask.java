package net.unibld.core.task.impl.sys;

import net.unibld.core.task.annotations.Task;

@Task(name="ssh",runnerClass=SshExecTaskRunner.class)
public class SshTask extends AbstractSshTask {
	private String remoteCommand;
	private boolean logOutput=true;

	public String getRemoteCommand() {
		return remoteCommand;
	}

	public void setRemoteCommand(String remoteCommand) {
		this.remoteCommand = remoteCommand;
	}

	public boolean isLogOutput() {
		return logOutput;
	}

	public void setLogOutput(boolean logOutput) {
		this.logOutput = logOutput;
	}
}
