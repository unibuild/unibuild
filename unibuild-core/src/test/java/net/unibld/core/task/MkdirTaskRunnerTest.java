package net.unibld.core.task;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.config.TaskContext;
import net.unibld.core.task.impl.sys.MkdirTask;
import net.unibld.core.task.impl.sys.MkdirTaskRunner;
import net.unibld.core.test.UnitTestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class MkdirTaskRunnerTest {
	@Autowired
	private MkdirTaskRunner runner;

	@BeforeClass
	public static void cleanup() {
		File dir = UnitTestContext.getFolder("existing","mkdir");
		if (dir.exists()) {
	 		try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Test
	public void testCreateDir() throws IOException {
		File dir = UnitTestContext.getFolder("existing","mkdir");
		dir.mkdirs();
		
		File newdir = new File(dir.getAbsolutePath()+"/mktest1");
		
		execute(newdir.getAbsolutePath(),true);
		
		Assert.assertTrue(newdir.exists()&&newdir.isDirectory());
		
	}

	private void execute(String path,boolean logging) {
		MkdirTask t=new MkdirTask();
		t.setContext(new TaskContext());
		
		t.setPath(path);
		
		runner.run(t);
	}
	
}
