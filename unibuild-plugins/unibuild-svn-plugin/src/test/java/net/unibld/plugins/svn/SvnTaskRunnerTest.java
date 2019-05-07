package net.unibld.plugins.svn;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.BuildGoalsConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.scm.RealmHelper;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.test.TestCredentialStoreFactory;
import net.unibld.core.test.UnitTestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/svn-test-context.xml"})
@Ignore
public class SvnTaskRunnerTest {
	
	private static final String SVN_USER = "test-user";
	private static final String SVN_REPO_URL = "http://ewise.hu/svn/svn-test";
	
	@Autowired
	private TestCredentialStoreFactory testCredentialStoreFactory;
	
	@Autowired
	private SvnTaskRunner runner;
	
	@Test
	public void testSvnCheckoutBuiltin() throws URISyntaxException, IOException {
		SvnTask t=new SvnTask();
		String folder = getFolder();
		
		t.setCheckoutDir(getCheckoutDir(folder));
		t.setCommand("checkout");
		runSvnTask(t, folder,true,false,true,false);
	}
	protected void runSvnTask(SvnTask t, String folder,boolean useBuiltInStore,boolean useTestCredentialStore,boolean passwdOk,boolean passwdAsked)
			throws URISyntaxException, IOException {
		t.setLogFile(FilenameUtils.concat(folder, "checkout.log"));
		t.setPasswordStrategy(PasswordStrategy.stored);
		t.setRepositoryUrl(SVN_REPO_URL);
		t.setUseBuiltinStore(String.valueOf(useBuiltInStore));
		t.setUserName(SVN_USER);
		t.setContext(new TaskContext());
		ProjectConfig pc = new ProjectConfig();
		pc.setGoalsConfig(new BuildGoalsConfig());
		t.getContext().setProjectConfig(pc);
		t.getContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, folder);
		
		SvnTaskRunner r=new SvnTaskRunner();
		r.setBuiltInStorePath(FilenameUtils.concat(folder, ".subversion"+File.separator+"svn-simple"));
		File storeDir = new File(r.getBuiltInStorePath());
		storeDir.mkdirs();
		if (useBuiltInStore) {
			String authFile=UnitTestContext.extractResource(getClass(), "/test.auth");
			FileUtils.copyFileToDirectory(new File(authFile), storeDir);
		}
		
		//(folder,SVN_USER,"test123",useTestCredentialStore,passwdOk,passwdAsked));
		testCredentialStoreFactory.setUser(SVN_USER);
		testCredentialStoreFactory.setPasswd("test123");
		testCredentialStoreFactory.setPasswordOk(passwdOk);
		testCredentialStoreFactory.setPasswordAsked(passwdAsked);
		runner.run(t);
	}
	
	@Test
	public void testSvnCheckoutFail() throws URISyntaxException, IOException {
		SvnTask t=new SvnTask();
		String folder = getFolder();
		
		t.setCheckoutDir(getCheckoutDir(folder));
		t.setCommand("checkout");
		
		Exception ex=null;
		try {
			runSvnTask(t, folder,false,true,false,true);
		} catch (Exception e) {
			ex=e;
			LoggerFactory.getLogger(getClass()).error("Checkout error",e);
		}
		
		Assert.assertNotNull(ex);
		Assert.assertNotNull(testCredentialStoreFactory);
		Assert.assertTrue(testCredentialStoreFactory.isRemoved("svn", 
				RealmHelper.createRealm(SVN_REPO_URL), SVN_USER));
		
	}
	protected String getCheckoutDir(String folder) {
		String checkoutDir=FilenameUtils.concat(folder, "checkout");
		new File(checkoutDir).mkdirs();
		return checkoutDir;
	}

	protected String getFolder() {
		return FilenameUtils.concat(
				FilenameUtils.concat(UnitTestContext.getBaseDir(),SvnTaskRunnerTest.class.getSimpleName()),
				String.valueOf(System.currentTimeMillis()));
	}
}
