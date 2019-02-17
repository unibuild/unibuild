package net.unibld.core.task.impl.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Runner class for {@link SignJarTask} which is capable of signing JARs using JDK's jarsigner utility.
 * @author andor
 *
 */
@Component
public class SignJarTaskRunner extends AbstractSignJarTaskRunner<SignJarTask> {
	private static final Logger LOG=LoggerFactory.getLogger(SignJarTaskRunner.class);

	
	@Override
	protected String getTaskName() {
		return "signjar";
	}


	
	
}
