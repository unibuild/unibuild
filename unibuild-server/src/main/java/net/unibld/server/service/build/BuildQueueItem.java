package net.unibld.server.service.build;

import net.unibld.core.log.IBuildLogger;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.security.IPasswordReaderInterface;

/**
 * A class that represents an item in the {@link BuildQueue}.
 * @author andor
 *
 */
public class BuildQueueItem {
	private String buildId;
	private Project project;
	private String goal;
	private BuildQueueListener listener;
	private boolean verbose;
	private boolean trace;
	private String userId;
	private IBuildLogger externalLogger;
	private LogConsumer logConsumer;
	private int buildNumber;
	private IPasswordReaderInterface passwordReader;
	
	public BuildQueueItem(Project project, String goal,BuildQueueListener l) {
		super();
		this.project = project;
		this.goal = goal;
		this.listener=l;
		this.buildNumber=project.getBuildNumber()+1;
	}
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
	public BuildQueueListener getListener() {
		return listener;
	}
	public void setListener(BuildQueueListener listener) {
		this.listener = listener;
	}
	public boolean isVerbose() {
		return verbose;
	}
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public boolean isTrace() {
		return trace;
	}
	public void setTrace(boolean trace) {
		this.trace = trace;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public IBuildLogger getExternalLogger() {
		return externalLogger;
	}
	public void setExternalLogger(IBuildLogger externalLogger) {
		this.externalLogger = externalLogger;
	}
	public LogConsumer getLogConsumer() {
		return logConsumer;
	}
	public void setLogConsumer(LogConsumer logConsumer) {
		this.logConsumer = logConsumer;
	}
	public String getBuildId() {
		return buildId;
	}
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
	public String getLabel() {
		return String.format("%s [%s] #%d",getProject().getName(),getGoal(),
				buildNumber);
	}
	public int getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}
	public IPasswordReaderInterface getPasswordReader() {
		return passwordReader;
	}
	public void setPasswordReader(IPasswordReaderInterface passwordReader) {
		this.passwordReader = passwordReader;
	}
}
