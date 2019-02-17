package net.unibld.client.desktop.service;

import net.unibld.core.build.ProgressListener;
import net.unibld.core.persistence.model.Project;

public class ClientGoalRequest {
	private Project project;
	private String goal;
	private IClientGoalRunnerUI ui;
	private String buildDir;
	private boolean traceEnabled;
	private ProgressListener progressListener;
	
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public String getGoal() {
		return goal;
	}
	public void setGoal(String goal) {
		this.goal = goal;
	}
	public IClientGoalRunnerUI getUi() {
		return ui;
	}
	public void setUi(IClientGoalRunnerUI ui) {
		this.ui = ui;
	}
	public String getBuildDir() {
		return buildDir;
	}
	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}
	public boolean isTraceEnabled() {
		return traceEnabled;
	}
	public void setTraceEnabled(boolean traceEnabled) {
		this.traceEnabled = traceEnabled;
	}
	public ProgressListener getProgressListener() {
		return progressListener;
	}
	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}
}
