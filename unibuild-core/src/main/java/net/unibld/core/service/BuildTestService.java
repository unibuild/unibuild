package net.unibld.core.service;

import java.util.List;

import net.unibld.core.BuildTask;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.BuildTestResult;
import net.unibld.core.persistence.model.BuildTestSuite;
import net.unibld.core.test.TestResults;

public interface BuildTestService {
	public void saveTestResults(String buildId, BuildTask task,TestResults results);

	public List<BuildTestSuite> getTestSuites(Build build);

	public List<BuildTestResult> getTestResults(BuildTestSuite suite);
}
