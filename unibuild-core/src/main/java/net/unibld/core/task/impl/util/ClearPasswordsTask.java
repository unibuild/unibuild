package net.unibld.core.task.impl.util;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;


/**
 * Clears the passwords stored in the software's credential stores.
 * @author andor
 *
 */
@Task(name="clear-passwords",runnerClass=ClearPasswordsTaskRunner.class)
public class ClearPasswordsTask extends BuildTask {

	
	private static final long serialVersionUID = -1554057221291754573L;
	
}
