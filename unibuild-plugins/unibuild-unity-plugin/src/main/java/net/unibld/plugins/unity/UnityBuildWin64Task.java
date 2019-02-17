package net.unibld.plugins.unity;

import net.unibld.core.task.annotations.Task;

/**
 * A task that runs a 64-bit Windows Unity build
 * @author andor
 *
 */
@Task(name="unity-build-win64",runnerClass=UnityBuildWin64TaskRunner.class)
public class UnityBuildWin64Task extends UnityWinTask {
	
	
	
	
	private static final long serialVersionUID = -5419551208895487084L;

	/**
	 * Default constructor
	 */
	public UnityBuildWin64Task() {
		super();
	}

		
}
