package net.unibld.core.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.TaskConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.log.IBuildLogger;
import net.unibld.core.log.LoggingScheme;
import net.unibld.core.log.SimpleConsoleLogger;
import net.unibld.core.log.SimpleFileLogger;
import net.unibld.core.task.impl.util.CleanTask;
import net.unibld.core.task.impl.util.CleanTaskRunner;
import net.unibld.core.test.UnitTestContext;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class CleanTaskRunnerTest {
	@Autowired
	private CleanTaskRunner runner;
	@Test
	public void testCleanExistingFolder() {
		File dir = UnitTestContext.getFolder("existing","clean");
		dir.mkdirs();
		
		execute(dir,true);
	}
	private void execute(File dir,boolean logging) {
		CleanTask t=new CleanTask();
		t.setTaskConfig(new TaskConfig());
		t.getTaskConfig().setTaskType("clean");
		t.getTaskConfig().setTaskContext(new TaskContext());
		t.getTaskConfig().getTaskContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, dir.getAbsolutePath());
		t.setDir(dir.getAbsolutePath());
		runner.run(t);
	}
	
	
}
