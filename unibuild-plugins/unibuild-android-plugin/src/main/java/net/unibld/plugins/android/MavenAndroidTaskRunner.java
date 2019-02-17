package net.unibld.plugins.android;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MavenAndroidTaskRunner extends AbstractMavenAndroidTaskRunner<MavenAndroidTask> {
	private static final Logger LOG=LoggerFactory.getLogger(MavenAndroidTaskRunner.class);

	@Override
	protected String getTaskName() {
		return "maven-android";
	}
	
	
	
}
