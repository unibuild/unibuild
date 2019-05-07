package net.unibld.core.task;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.TaskContext;
import net.unibld.core.task.impl.util.CleanTask;
import net.unibld.core.task.impl.util.CleanTaskRunner;
import net.unibld.core.test.UnitTestContext;

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
		t.setContext(new TaskContext());
		t.getContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, dir.getAbsolutePath());
		t.setDir(dir.getAbsolutePath());
		runner.run(t);
	}
	
	
}
