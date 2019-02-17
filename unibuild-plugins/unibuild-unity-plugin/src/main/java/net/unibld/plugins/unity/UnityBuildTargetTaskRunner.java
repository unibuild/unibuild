package net.unibld.plugins.unity;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;



/**
 * Executes a custom Unity build target.<br>
 * @author andor
 * @version 1.0
 */
@Component
public class UnityBuildTargetTaskRunner extends AbstractUnityTaskRunner<UnityBuildTargetTask> {
	
	@Override
	protected String getTaskName() {
		return "unity-build-target";
	}


	@Override
	protected String getCommand(UnityBuildTargetTask task) {
		if (StringUtils.isEmpty(task.getTarget())) {
			throw new BuildException("Target not specified");
		}
		return "buildTarget "+task.getTarget();
	}

	

}//end UnityBuildWin32TaskRunner