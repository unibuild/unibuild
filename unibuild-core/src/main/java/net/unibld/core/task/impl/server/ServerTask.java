package net.unibld.core.task.impl.server;

import net.unibld.core.task.impl.ExecTask;

public abstract class ServerTask extends ExecTask {
	private String command;
	private String instanceName;
	private int waitAfterStopSeconds=5;
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	
	
	public int getWaitAfterStopSeconds() {
		return waitAfterStopSeconds;
	}

	public void setWaitAfterStopSeconds(int waitAfterStopSeconds) {
		this.waitAfterStopSeconds = waitAfterStopSeconds;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
}
