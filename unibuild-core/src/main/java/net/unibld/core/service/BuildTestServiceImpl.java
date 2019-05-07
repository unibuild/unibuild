package net.unibld.core.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.unibld.core.BuildTask;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.BuildTestResult;
import net.unibld.core.persistence.model.BuildTestSuite;
import net.unibld.core.repositories.BuildRepository;
import net.unibld.core.repositories.BuildTestResultRepository;
import net.unibld.core.repositories.BuildTestSuiteRepository;
import net.unibld.core.task.TaskRegistry;
import net.unibld.core.test.TestResult;
import net.unibld.core.test.TestResults;
import net.unibld.core.test.TestSuite;

@Service("buildTestService")
public class BuildTestServiceImpl implements BuildTestService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BuildTestServiceImpl.class);
	
	@Autowired
	private BuildRepository buildRepo;
	
	@Autowired
	private TaskRegistry taskRegistry;
	
	@Autowired
	private BuildTestSuiteRepository suiteRepo;
	@Autowired
	private BuildTestResultRepository resultRepo;
	

	@Override
	@Transactional
	public void saveTestResults(String buildId, BuildTask task,TestResults results) {
		if (results==null) {
			throw new IllegalArgumentException("TestResults was null");
		}
		
		Build build = buildRepo.findOne(buildId);
		if (build==null) {
			throw new IllegalArgumentException("Invalid build id: "+buildId);
		}
		
		String taskName=taskRegistry.getTaskNameByClass(task.getClass());
		if (taskName==null) {
			throw new IllegalStateException("Invalid task class: "+task.getClass().getName());
		}
		
		LOGGER.info("Saving test results for: {}", task.getClass().getSimpleName());
		if (results.getSuites()!=null) {
			for (TestSuite ts:results.getSuites()) {
				BuildTestSuite s=new BuildTestSuite();
				s.setBuild(build);
				s.setId(UUID.randomUUID().toString());
				s.setName(ts.getName());
				s.setPackageName(ts.getPackageName());
				s.setNumberOfErrors(ts.getNumberOfErrors());
				s.setNumberOfFailures(ts.getNumberOfFailures());
				s.setNumberOfFlakes(ts.getNumberOfFlakes());
				s.setNumberOfSkipped(ts.getNumberOfSkipped());
				s.setNumberOfTests(ts.getNumberOfTests());
				s.setTimeElapsed(ts.getTimeElapsed());
				
				s.setTaskClass(task.getClass().getName());
				s.setTaskIdx(task.getContext().getTaskIndex());
				s.setTaskName(taskName);
				
				s=suiteRepo.save(s);
				
				if (ts.getResults()!=null) {
					for (TestResult tr:ts.getResults()) {
						BuildTestResult r=new BuildTestResult();
						r.setName(tr.getName());
						r.setFullClassName(tr.getFullClassName());
						r.setFailureDetail(tr.getFailureDetail());
						r.setFailureErrorLine(tr.getFailureErrorLine());
						r.setFailureMessage(tr.getFailureMessage());
						r.setFailureType(tr.getFailureType());
						r.setTime(tr.getTime());
						r.setSuite(s);
						resultRepo.save(r);
					}
				}
			}
		}
	}

}
