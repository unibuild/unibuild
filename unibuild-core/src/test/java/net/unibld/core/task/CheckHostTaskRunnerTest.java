package net.unibld.core.task;

import java.io.File;

import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.TaskConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.task.impl.sys.CheckHostTask;
import net.unibld.core.task.impl.sys.CheckHostTaskRunner;
import net.unibld.core.test.UnitTestContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class CheckHostTaskRunnerTest {
	@Autowired
	private CheckHostTaskRunner runner;
	
	@Test
	public void testCheckLocalhost() {
		File dir = UnitTestContext.getFolder("existing","check-host");
		dir.mkdirs();
		
		execute(dir,"localhost",true);
	}
	private void execute(File dir,String host,boolean logging) {
		CheckHostTask t=new CheckHostTask();
		t.setTaskConfig(new TaskConfig());
		t.getTaskConfig().setTaskType("check-host");
		t.getTaskConfig().setTaskContext(new TaskContext());
		t.getTaskConfig().getTaskContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, dir.getAbsolutePath());
		t.setHostName(host);
		
		runner.run(t);
	}
	
	@Test
	public void testCheckLocalhostIp() {
		File dir = UnitTestContext.getFolder("existing","check-host-ip");
		dir.mkdirs();
		
		execute(dir,"127.0.0.1",true);
	}
	
	
}
