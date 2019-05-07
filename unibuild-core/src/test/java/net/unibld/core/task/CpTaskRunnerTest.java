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
import net.unibld.core.task.impl.sys.CpTask;
import net.unibld.core.task.impl.sys.CpTaskRunner;
import net.unibld.core.test.UnitTestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class CpTaskRunnerTest {
	@Autowired
	private CpTaskRunner runner;
	@BeforeClass
	public static void cleanup() {
		File dir = UnitTestContext.getFolder("existing","cp");
		File dir2 = UnitTestContext.getFolder("existing.copy","cp");
		if (dir.exists()) {
	 		try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (dir2.exists()) {
	 		try {
				FileUtils.deleteDirectory(dir2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testCopyExistingFile() throws IOException {
		File dir = UnitTestContext.getFolder("existing","cp");
		dir.mkdirs();
		
		File orig = new File(dir.getAbsolutePath()+"/1.txt");
		FileUtils.writeStringToFile(orig, "test", "UTF-8");
		
		new File(orig.getAbsolutePath()+"/target").mkdirs();
		execute(orig,dir.getAbsolutePath()+"/target",true);
		
		File cpf = new File(dir.getAbsolutePath()+"/target/1.txt");
		Assert.assertTrue(cpf.exists());
		Assert.assertEquals(cpf.length(),orig.length());
	}
	@Test
	public void testCopyExistingFolder() {
		File dir = UnitTestContext.getFolder("existing","cp");
		dir.mkdirs();
		
		execute(dir,dir.getAbsolutePath()+"copy",true);
		
		File orig = new File(dir.getAbsolutePath()+"/1.txt");
		
		File cpd = new File(dir.getAbsolutePath()+"copy");
		File cpf = new File(dir.getAbsolutePath()+"copy/1.txt");
		Assert.assertTrue(cpd.exists()&&cpd.isDirectory());
		Assert.assertTrue(cpf.exists()&&cpf.isFile());
		Assert.assertEquals(cpf.length(),orig.length());
	}
	
	private void execute(File file,String target,boolean logging) {
		CpTask t=new CpTask();
		t.setContext(new TaskContext());
		//t.getTaskConfig().getTaskContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, dir.getAbsolutePath());
		t.setSource(file.getAbsolutePath());
		t.setTarget(target);
		runner.run(t);
	}
	
}
