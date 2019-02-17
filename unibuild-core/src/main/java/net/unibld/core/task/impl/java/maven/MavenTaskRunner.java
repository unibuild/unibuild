package net.unibld.core.task.impl.java.maven;

import org.springframework.stereotype.Component;


/**
 * Executes a {@link MavenTask} using Apache Maven and Apache Commons Exec.<br>
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:28
 */
@Component
public class MavenTaskRunner extends AbstractMavenTaskRunner<MavenTask> {
	
	@Override
	protected String getTaskName() {
		return "maven";
	}

}//end MavenTaskRunner