package net.unibld.plugins.innosetup;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ThirdPartyExecTask;


/**
 * A task that creates a setup.exe based on an InnoSetup configuration file (.iss)
 * @author andor
 *
 */
@Task(name="innosetup-make",runnerClass=InnoSetupMakeTaskRunner.class)
public class InnoSetupMakeTask extends ThirdPartyExecTask {
	
	private static final long serialVersionUID = 6751812383846520437L;
	private String issPath;
	private boolean consoleMode=true;

	public String getIssPath() {
		return issPath;
	}

	public void setIssPath(String issPath) {
		this.issPath = issPath;
	}

	public boolean isConsoleMode() {
		return consoleMode;
	}

	public void setConsoleMode(boolean consoleMode) {
		this.consoleMode = consoleMode;
	}
	
}
