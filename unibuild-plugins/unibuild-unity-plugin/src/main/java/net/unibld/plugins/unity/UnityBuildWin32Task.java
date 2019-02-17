package net.unibld.plugins.unity;

import net.unibld.core.task.annotations.Task;

/**
 * A task that runs a 32-bit Windows Unity build
 * @author andor
 *
 */
@Task(name="unity-build-win32",runnerClass=UnityBuildWin32TaskRunner.class)
public class UnityBuildWin32Task extends UnityWinTask {
	
	
	
	private static final long serialVersionUID = 3926179435114297154L;

	/**
	 * Default constructor
	 */
	public UnityBuildWin32Task() {
		super();
	}

		
}
