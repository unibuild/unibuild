package net.unibld.core.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.BuildGoalsConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.task.impl.java.SignJarTask;
import net.unibld.core.task.impl.java.SignJarTaskRunner;
import net.unibld.core.test.TestCredentialStoreFactory;
import net.unibld.core.test.UnitTestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
@Ignore
public class SignJarTaskRunnerTest {
	
	@Autowired
	private SignJarTaskRunner runner;
	@Autowired
	private TestCredentialStoreFactory testCredentialStoreFactory;

	
	@Test
	public void testSignJar() throws IOException {
		InputStream is = getClass().getResourceAsStream("/jarsigner/junit-to-sign.jar");
		if (is==null) {
			throw new IllegalArgumentException("Test jar not found");
		}
		InputStream is2 = getClass().getResourceAsStream("/jarsigner/test.keystore");
		if (is2==null) {
			throw new IllegalArgumentException("Test keystore not found");
		}
		
		File dir = UnitTestContext.getFolder("tmp","signjar");
		dir.mkdirs();
		
		File tosign=new File(FilenameUtils.concat(dir.getAbsolutePath(), "junit-to-sign.jar"));
		byte[] bytes = IOUtils.toByteArray(is);
		FileUtils.writeByteArrayToFile(tosign, bytes);
		
		File keystore=new File(FilenameUtils.concat(dir.getAbsolutePath(), "test.keystore"));
		byte[] bytes2 = IOUtils.toByteArray(is2);
		FileUtils.writeByteArrayToFile(keystore, bytes2);
		
		
		SignJarTask t=new SignJarTask();
		t.setDigestAlg("SunJCE");
		t.setSigAlg("SHA1");
		t.setInput(tosign.getAbsolutePath());
		t.setAlias("test_alias");
		t.setStorePasswordProtected(true);
		t.setKeystore(keystore.getAbsolutePath());
		String javaHome = System.getProperty("java.home");
		if (!javaHome.toLowerCase().contains("jre")) {
			throw new IllegalStateException("Must use a JDK");
		}
		t.setJdkDir(new File(javaHome).getParentFile().getAbsolutePath());
		runTask(t,dir.getAbsolutePath());
	}

	private void runTask(SignJarTask t,String folder) {
		t.setPasswordStrategy(PasswordStrategy.stored);

		t.setContext(new TaskContext());
		ProjectConfig pc = new ProjectConfig();
		pc.setGoalsConfig(new BuildGoalsConfig());
		t.getContext().setProjectConfig(pc);
		t.getContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, folder);
		
		SignJarTaskRunner r=new SignJarTaskRunner();
		//CredentialStoreUtils.getTestCredentialStoreFactory(folder,"test_alias","test12",true,true,false));
		testCredentialStoreFactory.setUser("test_alias");
		testCredentialStoreFactory.setPasswd("test12");
		testCredentialStoreFactory.setPasswordOk(true);
		testCredentialStoreFactory.setPasswordAsked(false);
		runner.run(t);
	}
}
