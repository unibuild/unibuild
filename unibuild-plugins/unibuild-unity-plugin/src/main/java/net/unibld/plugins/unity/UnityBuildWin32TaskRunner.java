package net.unibld.plugins.unity;

import org.springframework.stereotype.Component;



/**
 * Executes a 32-bit Windows Unity build.<br>
 * @author andor
 * @version 1.0
 */
@Component
public class UnityBuildWin32TaskRunner extends AbstractUnityTaskRunner<UnityBuildWin32Task> {
	
	@Override
	protected String getTaskName() {
		return "unity-build-win32";
	}


	@Override
	protected String getCommand(UnityBuildWin32Task task) {
		return "buildWindowsPlayer";
	}


}//end UnityBuildWin32TaskRunner