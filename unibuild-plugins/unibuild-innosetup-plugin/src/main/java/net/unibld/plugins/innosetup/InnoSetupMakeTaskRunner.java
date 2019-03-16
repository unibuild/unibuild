package net.unibld.plugins.innosetup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.ThirdPartyTaskRunner;


/**
 * A runner for {@link InnoSetupMakeTask} that create a setup.exe based on an Innosetup configuration file (.iis)
 * @author andor
 *
 */
@Component
public class InnoSetupMakeTaskRunner extends ThirdPartyTaskRunner<InnoSetupMakeTask> {
	private static final Logger LOG=LoggerFactory.getLogger(InnoSetupMakeTaskRunner.class);
	
	@Override
	protected ExecutionResult execute(InnoSetupMakeTask task) {
		logTask("Executing InnoSetup: {0}...",task.getIssPath());
		return super.execute(task);
		
	}

	

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "innosetup-make";
	}

	@Override
	protected String getExecutableName(InnoSetupMakeTask task) {
		return !task.isConsoleMode()?"Compil32.exe":"ISCC.exe";
		
	}



	@Override
	protected String getArguments(InnoSetupMakeTask task) {
		StringBuilder b=new StringBuilder();
		if (!task.isConsoleMode()) {
			b.append("/cc ");
		}
		b.append(task.getIssPath());
		return b.toString();
	}
}