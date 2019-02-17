package net.unibld.server.service.build;

import net.unibld.core.log.IBuildLogger;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.security.IPasswordReaderInterface;

/**
 * Request class for {@link BuildQueue}.
 * @author andor
 *
 */
public class BuildQueueRequest {
	private Project project;
	private String goal;
	private boolean trace;
	private BuildQueueListener listener;
	private IBuildLogger externalLogger;
	private LogConsumer logConsumer;
	private String userId;
	private IPasswordReaderInterface passwordReader;
	/**
	 * @return Project
	 */
	public Project getProject() {
		return project;
	}
	/**
	 * @return Goal to run
	 */
	public String getGoal() {
		return goal;
	}
	/**
	 * @return True if trace
	 */
	public boolean isTrace() {
		return trace;
	}
	/**
	 * @return Build queue listener
	 */
	public BuildQueueListener getListener() {
		return listener;
	}
	/**
	 * @return External logger
	 */
	public IBuildLogger getExternalLogger() {
		return externalLogger;
	}
	/**
	 * @return Log consumer
	 */
	public LogConsumer getLogConsumer() {
		return logConsumer;
	}
	/**
	 * @return User ID
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @return Password reader interface
	 */
	public IPasswordReaderInterface getPasswordReader() {
		return passwordReader;
	}
	
	public BuildQueueRequest withGoal(String goal) {
		this.goal=goal;
		return this;
	}
	
	public BuildQueueRequest withProject(Project project) {
		this.project=project;
		return this;
	}
	public BuildQueueRequest withTrace(boolean trace) {
		this.trace=trace;
		return this;
	}
	public BuildQueueRequest withListener(BuildQueueListener listener) {
		this.listener=listener;
		return this;
	}
	public BuildQueueRequest withLogConsumer(LogConsumer consumer) {
		this.logConsumer=consumer;
		return this;
	}
	public BuildQueueRequest withExternalLogger(IBuildLogger logger) {
		this.externalLogger=logger;
		return this;
	}
	public BuildQueueRequest withUserId(String userId) {
		this.userId=userId;
		return this;
	}
	public BuildQueueRequest withPasswordReader(IPasswordReaderInterface passwordReader) {
		this.passwordReader=passwordReader;
		return this;
	}
}
