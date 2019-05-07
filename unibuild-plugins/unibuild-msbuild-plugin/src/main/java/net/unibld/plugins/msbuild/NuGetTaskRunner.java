package net.unibld.plugins.msbuild;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.ThirdPartyTaskRunner;
import net.unibld.core.util.PlatformHelper;

@Component
public class NuGetTaskRunner extends ThirdPartyTaskRunner<NuGetTask>{

	@Override
	protected ExecutionResult execute(NuGetTask task) {
		String baseDirectory = task.getBaseDirectory();
		logTask("Executing NuGet in {0} with arguments: {1}...",baseDirectory!=null?baseDirectory:".", getArguments(task));
		
		return super.execute(task);
	}
	@Override
	protected String getExecutableName(NuGetTask task) {
		return PlatformHelper.isWindows()?"nuget.exe":"nuget";
	}

	@Override
	protected String getTaskName() {
		return "nuget";
	}

	protected String getArguments(NuGetTask task) {
		return String.format("%s %s",task.getCommand(),task.getArgs()).trim();
	}

	protected void validate(NuGetTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		if (StringUtils.isEmpty(task.getCommand())) {
			throw new BuildException("Command not specified");
		}
	}
}
