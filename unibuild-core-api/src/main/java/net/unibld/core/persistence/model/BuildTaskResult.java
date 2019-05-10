package net.unibld.core.persistence.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="build_task_result")
public class BuildTaskResult {
	/**
	 * Globally unique ID
	 */
	@Id
	@Column(nullable=false)
	private String id;
	
	/**
	 * Parent build
	 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private Build build;
	
	@Column(nullable=false, length=100)
	private String taskName;
	
	@Column(nullable=false, length=255)
	private String taskClass;
	
	@Column(nullable=false)
	private int taskIdx;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	

	/**
	 * Completion date of the build, regardless of its success.
	 */
	@Column(nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date completeDate;
	
	
	/**
	 * Error message if the build was unsuccessful.
	 */
	@Column(nullable=true)
	private String errorMessage;
	
	/**
	 * True if the build completed successfully, false otherwise
	 */
	@Column(nullable=false)
	private boolean successful;
	
	/**
	 * Stack trace of the error if the build was unsuccessful and stack trace is available.
	 */
	@Column(nullable=true,length=Build.MAX_STACK_TRACE_LEN)
	private String errorStackTrace;
	
	/**
	 * Path of a log file or null
	 */
	@Column(nullable=true,length=255)
	private String logFilePath;
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskClass() {
		return taskClass;
	}

	public void setTaskClass(String taskClass) {
		this.taskClass = taskClass;
	}

	public int getTaskIdx() {
		return taskIdx;
	}

	public void setTaskIdx(int taskIdx) {
		this.taskIdx = taskIdx;
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

	public String getErrorStackTrace() {
		return errorStackTrace;
	}

	public void setErrorStackTrace(String errorStackTrace) {
		this.errorStackTrace = errorStackTrace;
	}

	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	
	
}
