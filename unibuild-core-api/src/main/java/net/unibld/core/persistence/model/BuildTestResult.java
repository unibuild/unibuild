package net.unibld.core.persistence.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="build_test_result")
public class BuildTestResult {
	/**
	 * Globally unique ID
	 */
	@Id
	@Column(nullable=false)
	private String id;
	
	@Column(nullable=false, length=255)
	private String fullClassName;
	@Column(nullable=false, length=255)
	private String name;
	
	@Column(nullable=true)
	@Lob
	private String failureDetail;
	@Column(nullable=true, length=100)
	private String failureErrorLine;
	@Column(nullable=true, length=255)
	private String failureMessage;
	@Column(nullable=true, length=255)
	private String failureType;

	@Column(nullable=false)
	private float time;
	

	/**
	 * Parent test
	 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private BuildTestSuite suite; 
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	
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


	public String getFullClassName() {
		return fullClassName;
	}


	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getFailureDetail() {
		return failureDetail;
	}


	public void setFailureDetail(String failureDetail) {
		this.failureDetail = failureDetail;
	}


	public String getFailureErrorLine() {
		return failureErrorLine;
	}


	public void setFailureErrorLine(String failureErrorLine) {
		this.failureErrorLine = failureErrorLine;
	}


	public String getFailureMessage() {
		return failureMessage;
	}


	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}


	public String getFailureType() {
		return failureType;
	}


	public void setFailureType(String failureType) {
		this.failureType = failureType;
	}


	public float getTime() {
		return time;
	}


	public void setTime(float time) {
		this.time = time;
	}


	public BuildTestSuite getSuite() {
		return suite;
	}


	public void setSuite(BuildTestSuite suite) {
		this.suite = suite;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
