package net.unibld.core.test;

import java.util.List;

public class TestSuite {
	private String name;
	private String packageName;
	
	private int numberOfTests;
	private int numberOfErrors;

	private int numberOfFailures;

	private int numberOfFlakes;
	private int numberOfSkipped;
	private float timeElapsed;
	
	private List<TestResult> results;

	public List<TestResult> getResults() {
		return results;
	}

	public void setResults(List<TestResult> results) {
		this.results = results;
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

	public int getNumberOfTests() {
		return numberOfTests;
	}

	public void setNumberOfTests(int numberOfTests) {
		this.numberOfTests = numberOfTests;
	}

	public int getNumberOfSkipped() {
		return numberOfSkipped;
	}

	public void setNumberOfSkipped(int numberOfSkipped) {
		this.numberOfSkipped = numberOfSkipped;
	}
}
