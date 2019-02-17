package net.unibld.core.task.impl.sys;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ExecTask;

@Task(name="check-host",runnerClass=CheckHostTaskRunner.class)
public class CheckHostTask extends ExecTask {
	private String hostName;
	private boolean exitIfNotAvailable=true;
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public boolean isExitIfNotAvailable() {
		return exitIfNotAvailable;
	}
	public void setExitIfNotAvailable(boolean exitIfNotAvailable) {
		this.exitIfNotAvailable = exitIfNotAvailable;
	}
	
	
	
}
