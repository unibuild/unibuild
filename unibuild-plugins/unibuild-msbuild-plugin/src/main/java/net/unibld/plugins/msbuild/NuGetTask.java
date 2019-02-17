package net.unibld.plugins.msbuild;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ThirdPartyExecTask;

@Task(name="nuget", runnerClass=NuGetTaskRunner.class)
public class NuGetTask extends ThirdPartyExecTask {
	private String command;
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
}
