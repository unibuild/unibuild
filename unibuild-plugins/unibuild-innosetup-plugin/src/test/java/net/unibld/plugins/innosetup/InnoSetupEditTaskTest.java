package net.unibld.plugins.innosetup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.TaskConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.test.UnitTestContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/innosetup-test-context.xml"})
@Ignore
public class InnoSetupEditTaskTest {
	@Autowired
	private InnoSetupEditTaskRunner runner;

	
	private void execute(File dir,boolean logging) {
		InnoSetupEditTask t=new InnoSetupEditTask();
		t.setTaskConfig(new TaskConfig());
		t.getTaskConfig().setTaskType("innosetup-edit");
		t.getTaskConfig().setTaskContext(new TaskContext());
		t.getTaskConfig().getTaskContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, dir.getAbsolutePath());
		t.setIssPath(FilenameUtils.concat(dir.getAbsolutePath(),"test.iss"));
		t.setPomFile(FilenameUtils.concat(dir.getAbsolutePath(),"pom.xml"));
		t.setBuildNumberProperties(FilenameUtils.concat(dir.getAbsolutePath(),"buildNumber.properties"));
		t.setVersionDefineName("MyAppVersion");
		t.setBuildNumberPropertyName("buildNumber0");
		
		runner.run(t);
	}
	
	@Test
	public void testEditVersionInDefineFromPom() throws IOException {
		File dir = UnitTestContext.getFolder("existing","innosetup-edit");
		dir.mkdirs();
		
		extractInnosetupFile(dir,"pom.xml");
		extractInnosetupFile(dir,"test.iss");
		extractInnosetupFile(dir,"buildNumber.properties");
		
		execute(dir,true);
		
		File iss=new File(FilenameUtils.concat(dir.getAbsolutePath(),"test.iss"));
		Assert.assertTrue(iss.exists());
		Assert.assertTrue(iss.isFile());
		
		 List<String> lines = FileUtils.readLines(iss,"UTF-8");
		 Assert.assertNotSame(lines.size(), 0);
		 
		 boolean succeeded=false;
		 for (String line:lines) {
			 if (line.startsWith("#define")) {
				 String[] split = line.split(" ");
				 if (split[1].trim().equals("MyAppVersion")) {
					 Assert.assertEquals(split[2],"\"0.1.0.11\"");
					 succeeded=true;
				 }
			 }
		 }
		 Assert.assertTrue(succeeded);
	}

	protected void extractInnosetupFile(File dir,String fileName) throws IOException {
		InputStream pomIs=getClass().getResourceAsStream("/innosetup/"+fileName);
		Assert.assertNotNull(pomIs);
		
		byte[] pomBytes = IOUtils.toByteArray(pomIs);
		pomIs.close();
		FileUtils.writeByteArrayToFile(new File(FilenameUtils.concat(dir.getAbsolutePath(),fileName)), pomBytes);
	}
	
}
