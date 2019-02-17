package net.unibld.core.task.impl.util;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;

/**
 * A task that runs a remote build on a UniBuild server.
 * @author andor
 *
 */
@Task(name="remote-build",runnerClass=RemoteBuildTaskRunner.class)
public class RemoteBuildTask extends BuildTask {
	
	private static final long serialVersionUID = -5987336186243812646L;

	private String host;
	
	private String projectFile;
	private String buildDir;
	private String ticket;
	private String outputDir;
	private String outputFile;
	private String includes;
	private String excludes;
	
	private String goal;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	

	public String getIncludes() {
		return includes;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public String getExcludes() {
		return excludes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}


	public String getBuildDir() {
		return buildDir;
	}

	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}

	public String getProjectFile() {
		return projectFile;
	}

	public void setProjectFile(String projectFile) {
		this.projectFile = projectFile;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}
}
