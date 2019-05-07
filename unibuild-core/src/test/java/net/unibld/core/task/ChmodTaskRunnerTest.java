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
import net.unibld.core.task.impl.sys.ChmodTask;
import net.unibld.core.task.impl.sys.ChmodTaskRunner;
import net.unibld.core.test.UnitTestContext;
import net.unibld.core.util.PlatformHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class ChmodTaskRunnerTest {
	@Autowired
	private ChmodTaskRunner runner;
	
	@BeforeClass
	public static void cleanup() {
		if (PlatformHelper.isWindows()) {
			return;
		}
		File dir = UnitTestContext.getFolder("existing","chmod");
		if (dir.exists()) {
	 		try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Test
	public void testChmodExistingDir() throws IOException {
		if (PlatformHelper.isWindows()) {
			return;
		}
		File dir = UnitTestContext.getFolder("existing","chmod");
		dir.mkdirs();
		
		execute(dir,"744",false,true);
		
		Assert.assertTrue(dir.exists());
		Assert.assertTrue(dir.isDirectory());
	}
	
	@Test
	public void testChmodExistingDirRecursive() throws IOException {
		if (PlatformHelper.isWindows()) {
			return;
		}
		File dir = UnitTestContext.getFolder("existing/1","chmod");
		dir.mkdirs();
		
		execute(dir,"744",true,true);
		
		Assert.assertTrue(dir.exists());
		Assert.assertTrue(dir.isDirectory());
		Assert.assertTrue(dir.getParentFile().exists());
		Assert.assertTrue(dir.getParentFile().isDirectory());
	}
	
	
	private void execute(File file,String mode,boolean recursive,boolean logging) {
		ChmodTask t=new ChmodTask();
		t.setContext(new TaskContext());
		//t.getTaskConfig().getTaskContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, dir.getAbsolutePath());
		t.setPath(file.getAbsolutePath());
		t.setMode(mode);
		t.setRecursive(recursive?"true":"false");
		
		runner.run(t);
	}
		
}
