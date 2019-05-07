package net.unibld.core.task.impl.java.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.maven.plugins.surefire.report.ReportTestCase;
import org.apache.maven.plugins.surefire.report.ReportTestSuite;
import org.apache.maven.plugins.surefire.report.SurefireReportParser;
import org.apache.maven.reporting.MavenReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.test.TestResult;
import net.unibld.core.test.TestResults;
import net.unibld.core.test.TestSuite;

@Component
public class SurefireTestResultParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(SurefireTestResultParser.class);

	public TestResults parseTestResults(String dirPath) {
		LOGGER.info("Parsing surefire reports directory for test results: {}...", dirPath);
		File dir=new File(dirPath);
		if (!dir.exists()||!dir.isDirectory()) {
			LOGGER.info("Surefire directory does not exist: {}", dirPath);
			return null;
		}
		
		 SurefireReportParser parser = new SurefireReportParser(
	                Arrays.asList(dir), Locale.getDefault(), null);
		 try {
			List<ReportTestSuite> reports = parser.parseXMLReportFiles();
			return toTestResults(reports);
		} catch (MavenReportException e) {
			LOGGER.error("Failed to parse surefire results",e);
			return null;
		}
	      
	}

	private TestResults toTestResults(List<ReportTestSuite> reports) {
		TestResults tr=new TestResults(); 
		tr.setSuites(new ArrayList<>() );	
		for (ReportTestSuite suite:reports) {
			tr.getSuites().add(toTestSuite(suite));
		}
		return tr;
	}

	private TestSuite toTestSuite(ReportTestSuite suite) {
		TestSuite t=new TestSuite();
		t.setName(suite.getName());
		t.setPackageName(suite.getPackageName());
		t.setNumberOfErrors(suite.getNumberOfErrors());
		t.setNumberOfFailures(suite.getNumberOfFailures());
		t.setNumberOfFlakes(suite.getNumberOfFlakes());
		t.setNumberOfSkipped(suite.getNumberOfSkipped());
		t.setNumberOfTests(suite.getNumberOfTests());
		t.setTimeElapsed(suite.getTimeElapsed());
		
		t.setResults(toTestResultsList(suite.getTestCases()));
		return t;
	}

	private List<TestResult> toTestResultsList(List<ReportTestCase> testCases) {
		List<TestResult> ret=new ArrayList<>();
		if (testCases!=null) {
			for (ReportTestCase tc:testCases) {
				ret.add(toTestResult(tc));
			}
		}
		return ret;
	}

	private TestResult toTestResult(ReportTestCase tc) {
		TestResult r=new TestResult();
		r.setFullClassName(tc.getFullClassName());
		r.setName(tc.getName());
		r.setFailureDetail(tc.getFailureDetail());
		r.setFailureErrorLine(tc.getFailureErrorLine());
		r.setFailureMessage(tc.getFailureMessage());
		r.setFailureType(tc.getFailureType());
		r.setTime(tc.getTime());
		return r;
	}
}
