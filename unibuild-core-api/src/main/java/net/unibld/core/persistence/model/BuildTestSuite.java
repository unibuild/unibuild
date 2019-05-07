package net.unibld.core.persistence.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="build_test_suite")
public class BuildTestSuite {
	/**
	 * Globally unique ID
	 */
	@Id
	@Column(nullable=false)
	private String id;
	
	@Column(nullable=false, length=255)
	private String name;

	@Column(nullable=false, length=255)
	private String packageName;

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
	private Date createDate;
	
	@Column(nullable=false)
	private int numberOfErrors;

	@Column(nullable=false)
	private int numberOfFailures;

	@Column(nullable=false)
	private int numberOfFlakes;
	
	@Column(nullable=false)
	private int numberOfSkipped;
	
	@Column(nullable=false)
	private int numberOfTests;
	
	@Column(nullable=false)
	private float timeElapsed;
	
	@PrePersist
	public void prePersist() {
		this.createDate=new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getNumberOfErrors() {
		return numberOfErrors;
	}

	public void setNumberOfErrors(int numberOfErrors) {
		this.numberOfErrors = numberOfErrors;
	}

	public int getNumberOfFailures() {
		return numberOfFailures;
	}

	public void setNumberOfFailures(int numberOfFailures) {
		this.numberOfFailures = numberOfFailures;
	}

	public int getNumberOfFlakes() {
		return numberOfFlakes;
	}

	public void setNumberOfFlakes(int numberOfFlakes) {
		this.numberOfFlakes = numberOfFlakes;
	}

	public float getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(float timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	public int getNumberOfSkipped() {
		return numberOfSkipped;
	}

	public void setNumberOfSkipped(int numberOfSkipped) {
		this.numberOfSkipped = numberOfSkipped;
	}

	public int getNumberOfTests() {
		return numberOfTests;
	}

	public void setNumberOfTests(int numberOfTests) {
		this.numberOfTests = numberOfTests;
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

}
