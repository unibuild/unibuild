package net.unibld.core.test;

public class TestResult {
	private String fullClassName;
	private String name;
	
	private String failureDetail;
	private String failureErrorLine;
	private String failureMessage;
	private String failureType;

	private float time;
	
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
}
