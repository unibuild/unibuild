package net.unibld.plugins.msbuild;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ExecTask;

/**
 * A task that executes a .NET build using Microsoft msbuild.exe
 * @author andor
 *
 */
@Task(name="msbuild", runnerClass=MsBuildTaskRunner.class)
public class MsBuildTask extends ExecTask {
	/**
	 * .NET csproj file
	 */
	private String project;
	/**
	 * Target(s) to execute. Multiple targets can be separated by the ';' character
	 */
	private String target;
	/**
	 * The .NET framework directory where msbuild.exe resides. If not specified, msbuild.exe is invoked
	 * without any path.
	 */
	private String frameworkDir;
	
	/**
	 * Configuration of the build, such as Debug or Release. If not specified, the default configuration is used.
	 */
	private String configuration;
	/**
	 * Platform of the build, such as AnyCPU or X64. If not specified, the default platform is used.
	 */
	private String platform;
	/**
	 * Extra parameter(s) of the build in key=value format. Multiple parameters can be separated by the ';' character
	 */
	private String parameters;
	
	/**
	 * @return .NET csproj file
	 */
	public String getProject() {
		return project;
	}
	/**
	 * @param project .NET csproj file
	 */
	public void setProject(String project) {
		this.project = project;
	}
	/**
	 * @return Target(s) to execute. Multiple targets can be separated by the ';' character
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target Target(s) to execute. Multiple targets can be separated by the ';' character
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return The .NET framework directory where msbuild.exe resides. If not specified, msbuild.exe is invoked
	 * without any path.
	 */
	public String getFrameworkDir() {
		return frameworkDir;
	}
	/**
	 * @param frameworkDir The .NET framework directory where msbuild.exe resides. If not specified, msbuild.exe is invoked
	 * without any path.
	 */
	public void setFrameworkDir(String frameworkDir) {
		this.frameworkDir = frameworkDir;
	}
	/**
	 * @return Configuration of the build, such as Debug or Release. If not specified, the default configuration is used.
	 */
	public String getConfiguration() {
		return configuration;
	}
	/**
	 * @param configuration Configuration of the build, such as Debug or Release. If not specified, the default configuration is used.
	 */
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	/**
	 * @return Platform of the build, such as AnyCPU or X64. If not specified, the default platform is used.
	 */
	public String getPlatform() {
		return platform;
	}
	/**
	 * @param platform Platform of the build, such as AnyCPU or X64. If not specified, the default platform is used.
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	/**
	 * @return Extra parameter(s) of the build in key=value format. Multiple parameters can be separated by the ';' character
	 */
	public String getParameters() {
		return parameters;
	}
	/**
	 * @param parameters Extra parameter(s) of the build in key=value format. Multiple parameters can be separated by the ';' character
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
}
