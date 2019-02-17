package net.unibld.core.build;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import net.unibld.core.test.UnitTestContext;

/**
 * A test class for testing basic use cases of the {@link Bootstrap} class,
 * the Java launcher of UniBuild 
 * @author andor
 *
 */
public class BootstrapTest {
	private static final Logger LOG=org.slf4j.LoggerFactory.getLogger(BootstrapTest.class);
	/**
	 * A method that is run before each test method execution. It stores
	 * original user directory path on the first run
	 */
	@Before
	public void beforeExecution() {
		UnitTestContext.initialize();
	}
	/**
	 * A method that is run after each test method execution. It cleans
	 * system properties to be able to emulate more bootstraps
	 */
	@After
	public void afterExecution() {
		System.clearProperty(BuildConstants.VARIABLE_NAME_PROJECT_DIR);
		System.clearProperty(BuildConstants.VARIABLE_NAME_BUILD_DIR);
		
		
	
	}
	/**
	 * Tests default start failure due to a non-existing project.xml file
	 * in the current directory
	 * @throws Exception The error expected
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartNormal() throws Exception {
		LOG.info("Bootsrapping non-existent project");
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		LOG.info("Non-existent project dir: {}",confDir.getAbsolutePath());
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		try {
			b.start(new String[]{"-c",confDir.getAbsolutePath()});
		} catch (Exception ex) {
			Assert.assertEquals(b.getParameters().isTrace(), false);
			Assert.assertEquals(b.getParameters().isVerbose(), false);
			Assert.assertNotNull(ex.getMessage());
			//Assert.assertTrue(ex.getMessage().startsWith(BuildConstants.ERR_PROJECT_FILE_DOES_NOT_EXIST));
			assertBuildContext(b.getContext(),null,null);
			throw ex;
		}
	}

	private void assertBuildContext(BuildToolContext context,String projFile,String projDir) {
		Assert.assertNotNull(context);
		Assert.assertNotNull(context.getProjectFile());
		if (projFile==null) {
			Assert.assertTrue(context.getProjectFile().endsWith("project.xml"));
		} else {
			Assert.assertEquals(context.getProjectFile(), projFile);
		}
		
		Assert.assertNotNull(context.getProjectDir());
		if (projDir==null&&projFile==null) {
			Assert.assertEquals(context.getProjectDir(), ".");
		} else if (projDir==null){
			Assert.assertEquals(new File(context.getProjectDir()).getAbsolutePath(), new File(projFile).getParent());
		} else {
			Assert.assertEquals(context.getProjectDir(), projDir);
		}
		
		Assert.assertNotNull(context.getBuildDir());
	}

	/**
	 * Tests default start failure due to a non-existing project.xml file
	 * in the current directory, with the trace switch on (-t)
	 * @throws Exception The error expected
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartTrace() throws Exception {
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		try {
			b.start(new String[]{"-t","-c",confDir.getAbsolutePath()});
		} catch (Exception ex) {
			Assert.assertEquals(b.getParameters().isTrace(), true);
			Assert.assertEquals(b.getParameters().isVerbose(), false);
			Assert.assertNotNull(ex.getMessage());
			//Assert.assertTrue(ex.getMessage().startsWith(BuildConstants.ERR_PROJECT_FILE_DOES_NOT_EXIST));

			throw ex;
		}
	}
	
	/**
	 * Tests default start failure due to a non-existing project.xml file
	 * in the current directory, with the verbose switch on (-v)
	 * @throws Exception The error expected
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartVerbose() throws Exception {
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		try {
			b.start(new String[]{"-v","-c",confDir.getAbsolutePath()});
		} catch (IllegalArgumentException ex) {
			Assert.assertEquals(b.getParameters().isVerbose(), true);
			Assert.assertEquals(b.getParameters().isTrace(), false);
			Assert.assertNotNull(ex.getMessage());
			//Assert.assertTrue(ex.getMessage().startsWith(BuildConstants.ERR_PROJECT_FILE_DOES_NOT_EXIST));
			assertBuildContext(b.getContext(),null,null);
			throw ex;
		}
	}
	
	@Test
	public void testStartProjectFileUniBuildPath() throws BuildException, URISyntaxException, IOException {
		String projectDir = UnitTestContext.getProjectDir();
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		String projectFileExisting = ProjectFileTestHelper.getProjectFileExisting();
		
		try {
			b.start(new String[]{"-f",projectFileExisting,"-c",confDir.getAbsolutePath()});
		} catch (BuildException e ) {
			e.printStackTrace();
		}
		Assert.assertEquals(b.getParameters().isTrace(), false);
		Assert.assertEquals(b.getParameters().isVerbose(), false);
		
		assertBuildContext(b.getContext(), projectFileExisting, null);
		
	}
	
	
	@Test
	public void testStartProjectFile() throws BuildException, URISyntaxException, IOException {
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		String projectFileExisting = ProjectFileTestHelper.getProjectFileExisting();
		
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		try {
			b.start(new String[]{"-f",projectFileExisting,"-c",confDir.getAbsolutePath()});
		} catch (BuildException e ) {
			e.printStackTrace();
		}
		Assert.assertEquals(b.getParameters().isTrace(), false);
		Assert.assertEquals(b.getParameters().isVerbose(), false);
		assertBuildContext(b.getContext(),projectFileExisting,null);
		
	}
	
	

	@Test
	public void testStartProjectFileGoal() throws BuildException, URISyntaxException, IOException {
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		String projectFileExisting = ProjectFileTestHelper.getProjectFileExisting();
		
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		try {
			b.start(new String[]{"-f",projectFileExisting,"-c",confDir.getAbsolutePath(),"dump"});
		} catch (BuildException e ) {
			e.printStackTrace();
		}
		Assert.assertEquals(b.getParameters().isTrace(), false);
		Assert.assertEquals(b.getParameters().isVerbose(), false);
		//assertBuildContext(b.getContext(),projectFileExisting,null,nativeLibDir);
		
		Assert.assertEquals(b.getParameters().getGoal(), "dump");
	
	}
	@Test(expected=BuildException.class)
	public void testStartProjectFileEmpty() throws Exception {
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		try {
			b.start(new String[]{"-f",ProjectFileTestHelper.getProjectFileExistingEmpty(),"-c",confDir.getAbsolutePath()});
		} catch (BuildException e) {
			Assert.assertEquals(b.getParameters().isTrace(), false);
			Assert.assertEquals(b.getParameters().isVerbose(), false);
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().equals(BuildConstants.ERR_NO_TASKS));
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testStartProjectFileNotExisting() throws BuildException, URISyntaxException {
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		b.start(new String[]{"-f",ProjectFileTestHelper.getProjectFileNotExisting(),"-c",confDir.getAbsolutePath()});
		
	}
	@Test
	public void testStartProjectDir() throws BuildException, URISyntaxException, IOException {
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		String projectFileExisting = ProjectFileTestHelper.getProjectFileExisting();
		String projectDir = ProjectFileTestHelper.getBuildProjectDir();
		try {
			
			b.start(new String[]{"-f",projectFileExisting,"-d",projectDir,"-c",confDir.getAbsolutePath()});
		} catch (BuildException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(b.getParameters().isTrace(), false);
		Assert.assertEquals(b.getParameters().isVerbose(), false);
//		assertBuildContext(b.getContext(), projectFileExisting, projectDir,null);
	}
	
	@Test
	public void testStartProjectFileVerbose() throws Exception {
		File confDir = UnitTestContext.getFolder("config", "bootstrap");
		confDir.mkdirs();
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		try {
			b.start(new String[]{"-v","-f",ProjectFileTestHelper.getProjectFileExisting(),"-c",confDir.getAbsolutePath()});
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		Assert.assertEquals(b.getParameters().isVerbose(), true);
		Assert.assertEquals(b.getParameters().isTrace(), false);
	}



}
