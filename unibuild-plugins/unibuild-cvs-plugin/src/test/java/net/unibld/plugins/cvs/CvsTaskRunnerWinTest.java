package net.unibld.plugins.cvs;

import java.io.File;
import java.io.IOException;

import net.unibld.core.config.TaskConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.test.UnitTestContext;
import net.unibld.plugins.cvs.CvsTask;
import net.unibld.plugins.cvs.CvsTaskRunner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/cvs-test-context.xml"})
@Ignore
public class CvsTaskRunnerWinTest {
	@Autowired
	private CvsTaskRunner runner;
	
	@Test
	public void testCheckoutRemote() throws IOException {
		File dir = new File("./target/cvs");
		dir.mkdirs();
		
		
		File thirdDir = new File(UnitTestContext.getProjectDir()+"/dist/3rdparty");
		
		execute(dir,thirdDir,CvsTask.CONNECTION_TYPE_EXT,"checkout","example.com","/srv/cvs/","example","example-user",PasswordStrategy.ask,true);
	}
	
	private void execute(File nativeDir,File thirdPartyDir,String connectionType,String command,String host,String path,String module,String userName,PasswordStrategy strategy,boolean logging) throws IOException {
		File folder = UnitTestContext.getFolder(command,"cvs");
		folder.mkdirs();
	
		File log4cpp=new File("./log4cpp.properties");
		String log4cppPath=log4cpp.getAbsolutePath();
		
	
		FileUtils.copyFile(new File(log4cppPath), new File(FilenameUtils.concat(folder.getAbsolutePath(),"log4cpp.properties")));
		
		
		CvsTask t=new CvsTask();
		t.setCommand(command);
		t.setCheckoutRelativePath(module);
		t.setCheckoutDir(UnitTestContext.getBaseDir()+"/cvs");
		
		t.setConnectionType(connectionType);
		t.setUserName(userName);
		t.setHostName(host);
		t.setRepositoryPath(path);
		t.setModule(module);
		t.setPasswordStrategy(strategy);
		t.setTaskConfig(new TaskConfig());
		t.getTaskConfig().setTaskType("cvs");
		t.getTaskConfig().setTaskContext(new TaskContext());
		
		runner.run(t);
	}
	
	
}
