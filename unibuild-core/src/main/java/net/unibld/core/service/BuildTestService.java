package net.unibld.core.service;

import net.unibld.core.BuildTask;
import net.unibld.core.test.TestResults;

public interface BuildTestService {
	public void saveTestResults(String buildId, BuildTask task,TestResults results);
}
