package net.unibld.plugins.unity;

import org.springframework.stereotype.Component;



/**
 * Executes a 64-bit Windows Unity build.<br>
 * @author andor
 * @version 1.0
 *
 */
@Component
public class UnityBuildWin64TaskRunner extends AbstractUnityTaskRunner<UnityBuildWin64Task> {
	
	@Override
	protected String getTaskName() {
		return "unity-build-win64";
	}



	@Override
	protected String getCommand(UnityBuildWin64Task task) {
		return "buildWindows64Player";
	}


}//end UnityBuildWin64TaskRunner