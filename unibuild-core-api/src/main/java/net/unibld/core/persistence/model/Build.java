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

/**
 * An entity that represents a single run of a build goal.
 * @author andor
 *
 */
@Entity
@Table(name="build")
public class Build {
	/**
	 * Maximum length of the stack trace field
	 */
	public static final int MAX_STACK_TRACE_LEN = 3000;



	/**
	 * Globally unique ID
	 */
	@Id
	@Column(nullable=false)
	private String id;

		
	
	@Column(nullable=true)
	private String creatorUser;

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
	 * Date of user cancellation or null
	 */
	@Column(nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date cancelDate;
	
	
	/**
	 * Parent project
	 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	private Project project; 
	
	/**
	 * Increasing build number per project
	 */
	@Column(nullable=false)
	private int buildNumber;
	
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
	
	@Column(nullable=true)
	private String buildDir;
	@Column(nullable=true)
	private String productName;
	/**
	 * Number of tasks to execute
	 */
	@Column(nullable=false)
	private int taskCount;
	/**
	 * Goal executed
	 */
	@Column(nullable=false)
	private String goal;

	/**
	 * Stack trace of the error if the build was unsuccessful and stack trace is available.
	 */
	@Column(nullable=true,length=MAX_STACK_TRACE_LEN)
	private String errorStackTrace;
	
	/**
	 * Simple name of the failed task class if the build was unsuccessful.
	 */
	@Column(nullable=true)
	private String failedTaskClass;
	/**
	 * Index of the failed task if the build was unsuccessful.
	 */
	@Column(nullable=true)
	private Integer failedTaskIndex;
	
	
	/**
	 * Path of a log file or null
	 */
	@Column(nullable=true,length=255)
	private String logFilePath;
	
	/**
	 * @return Globally unique ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id Globally unique ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Starter of the build
	 */
	public String getCreatorUser() {
		return creatorUser;
	}

	/**
	 * @param creatorUser Starter of the build
	 */
	public void setCreatorUser(String creatorUser) {
		this.creatorUser = creatorUser;
	}

	/**
	 * @return Start time of the build run
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate Start time of the build run
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return Completion date of the build, regardless of its success.
	 */
	public Date getCompleteDate() {
		return completeDate;
	}

	/**
	 * @param completeDate Completion date of the build, regardless of its success.
	 */
	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	/**
	 * @return Parent project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project Parent project
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return Error message if the build was unsuccessful.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage Error message if the build was unsuccessful.
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return True if the build completed successfully, false otherwise
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @param successful True if the build completed successfully, false otherwise
	 */
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	/**
	 * @return Build directory
	 */
	public String getBuildDir() {
		return buildDir;
	}

	/**
	 * @param buildDir Build directory
	 */
	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}

	

	/**
	 * @return Name of the product to build
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName Name of the product to build
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return Number of tasks to execute
	 */
	public int getTaskCount() {
		return taskCount;
	}

	/**
	 * @param taskCount Number of tasks to execute
	 */
	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	/**
	 * @return Goal executed
	 */
	public String getGoal() {
		return goal;
	}

	/**
	 * @param goal Goal executed
	 */
	public void setGoal(String goal) {
		this.goal = goal;
	}

	/**
	 * @return Stack trace of the error if the build was unsuccessful and stack trace is available.
	 */
	public String getErrorStackTrace() {
		return errorStackTrace;
	}

	/**
	 * @param errorStackTrace Stack trace of the error if the build was unsuccessful and stack trace is available.
	 */
	public void setErrorStackTrace(String errorStackTrace) {
		this.errorStackTrace = errorStackTrace;
	}

	/**
	 * @return Simple name of the failed task class if the build was unsuccessful.
	 */
	public String getFailedTaskClass() {
		return failedTaskClass;
	}

	/**
	 * @param failedTaskClass Simple name of the failed task class if the build was unsuccessful.
	 */
	public void setFailedTaskClass(String failedTaskClass) {
		this.failedTaskClass = failedTaskClass;
	}

	/**
	 * @return Index of the failed task if the build was unsuccessful.
	 */
	public Integer getFailedTaskIndex() {
		return failedTaskIndex;
	}

	/**
	 * @param failedTaskIndex Index of the failed task if the build was unsuccessful.
	 */
	public void setFailedTaskIndex(Integer failedTaskIndex) {
		this.failedTaskIndex = failedTaskIndex;
	}

	/**
	 * @return Increasing build number per project
	 */
	public int getBuildNumber() {
		return buildNumber;
	}

	/**
	 * @param buildNumber Increasing build number per project
	 */
	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	/**
	 * @return Path of a log file or null
	 */
	public String getLogFilePath() {
		return logFilePath;
	}

	/**
	 * @param logFilePath Path of a log file or null
	 */
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	/**
	 * @return Date of user cancellation or null
	 */
	public Date getCancelDate() {
		return cancelDate;
	}

	/**
	 * @param cancelDate Date of user cancellation or null
	 */
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
}
