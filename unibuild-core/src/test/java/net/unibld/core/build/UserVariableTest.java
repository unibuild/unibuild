package net.unibld.core.build;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.unibld.core.test.UnitTestContext;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class UserVariableTest {
	@Test
	public void testUserDefinedVar() throws BuildException, URISyntaxException, IOException {
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		String projectFileExisting = ProjectFileTestHelper.getProjectFile("/var/project-var.xml");
		
		File confDir = UnitTestContext.getFolder("config", "var");
		confDir.mkdirs();
		
		try {
			b.start(new String[]{"-f",projectFileExisting,"-c",confDir.getAbsolutePath()});
		} catch (BuildException e ) {
			e.printStackTrace();
		}
		Assert.assertEquals(b.getParameters().isTrace(), false);
		Assert.assertEquals(b.getParameters().isVerbose(), false);
		
		Assert.assertNotNull(b.getContext().getVariables());
	}
}
