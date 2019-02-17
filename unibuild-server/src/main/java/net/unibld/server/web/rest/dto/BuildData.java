package net.unibld.server.web.rest.dto;

import java.util.Date;

public class BuildData {
	private String id;
	private String projectId;
	private String projectName;
	private String user;

	private Date startDate;
	
	private Date completeDate;
	
	private String errorMessage;
	
	private boolean successful;
	
	private String buildDir;
	private int taskCount;
	private String goal;
	private Integer failedTaskIndex;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getBuildDir() {
		return buildDir;
	}

	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public Integer getFailedTaskIndex() {
		return failedTaskIndex;
	}

	public void setFailedTaskIndex(Integer failedTaskIndex) {
		this.failedTaskIndex = failedTaskIndex;
	}
}
