package net.unibld.plugins.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
import net.unibld.core.test.TestCredentialStoreFactory;
import net.unibld.core.test.UnitTestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
@Ignore
public class AndroidSignApkTaskRunnerTest {
	
	@Autowired
	private AndroidSignApkTaskRunner runner;
	@Autowired
	private TestCredentialStoreFactory testCredentialStoreFactory;

	
	@Test
	public void testSignJar() throws IOException {
		InputStream is = getClass().getResourceAsStream("/signapk/Test.apk");
		if (is==null) {
			throw new IllegalArgumentException("Test jar not found");
		}
		InputStream is2 = getClass().getResourceAsStream("/signapk/test.keystore");
		if (is2==null) {
			throw new IllegalArgumentException("Test keystore not found");
		}
		
		File dir = UnitTestContext.getFolder("tmp","signapk");
		dir.mkdirs();
		
		File tosign=new File(FilenameUtils.concat(dir.getAbsolutePath(), "Test.apk"));
		byte[] bytes = IOUtils.toByteArray(is);
		FileUtils.writeByteArrayToFile(tosign, bytes);
		
		
		InputStream antIs = getClass().getResourceAsStream("/signapk/ant.properties");
		if (antIs==null) {
			throw new IllegalArgumentException("Ant properties not found");
		}
		
		Properties antProps=new Properties();
		
		
		File keystore=new File(FilenameUtils.concat(dir.getAbsolutePath(), "test.keystore"));
		byte[] bytes2 = IOUtils.toByteArray(is2);
		FileUtils.writeByteArrayToFile(keystore, bytes2);
		
		
		antProps.setProperty("key.store", keystore.getAbsolutePath());
		
		File antfile=new File(FilenameUtils.concat(dir.getAbsolutePath(), "ant.properties"));
		FileOutputStream antFos = new FileOutputStream(antfile);
		try {
			antProps.store(antFos, "");
		} finally {
			antFos.close();
		}
		
		AndroidSignApkTask t=new AndroidSignApkTask();
		t.setDigestAlg("SHA1withRSA");
		t.setSigAlg("SHA1");
		t.setInput(tosign.getAbsolutePath());
		t.setAntProperties(antfile.getAbsolutePath());
		t.setStorePasswordProtected(true);
		t.setStorePasswordProtected(true);
		t.setAlreadySigned(true);
		t.setOutput(FilenameUtils.concat(dir.getAbsolutePath(), "output.apk"));
		runTask(t,dir.getAbsolutePath());
	}

	private void runTask(AndroidSignApkTask t,String folder) {
		t.setPasswordStrategy(PasswordStrategy.stored);

		t.setContext(new TaskContext());
		ProjectConfig pc = new ProjectConfig();
		pc.setGoalsConfig(new BuildGoalsConfig());
		t.getContext().setProjectConfig(pc);
		t.getContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, folder);
		
		testCredentialStoreFactory.setUser("test_alias");
		testCredentialStoreFactory.setPasswd("test12");
		testCredentialStoreFactory.setPasswordOk(true);
		testCredentialStoreFactory.setPasswordAsked(false);
		
		try {
			runner.run(t);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
