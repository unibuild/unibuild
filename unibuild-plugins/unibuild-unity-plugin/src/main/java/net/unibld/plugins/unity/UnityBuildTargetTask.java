package net.unibld.plugins.unity;

import net.unibld.core.task.annotations.Task;

/**
 * A task that runs an Android Unity build
 * @author andor
 *
 */
@Task(name="unity-build-target",runnerClass=UnityBuildTargetTaskRunner.class)
public class UnityBuildTargetTask extends UnityTask {
	
	private String target;
	/**
	 * Default constructor
	 */
	public UnityBuildTargetTask() {
		super();
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

		
}
