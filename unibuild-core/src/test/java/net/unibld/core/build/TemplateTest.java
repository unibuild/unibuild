package net.unibld.core.build;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.plugin.PluginDetector;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class TemplateTest {
	@Autowired
	private PluginDetector pluginDetector;
	@Test
	public void testTemplates() throws URISyntaxException, IOException  {
		File dir=new File(pluginDetector.getPluginDir());
		dir.mkdirs();
		
		Bootstrap b=new Bootstrap();
		b.setTestMode(true);
		String projectFileExisting = ProjectFileTestHelper.getProjectFile("/template/project-template.xml");
		
		try {
			b.start(new String[]{"-f",projectFileExisting});
		} catch (BuildException e ) {
			e.printStackTrace();
		}
		Assert.assertFalse(b.getParameters().isTrace());
		Assert.assertFalse(b.getParameters().isVerbose());
		
		//Assert.assertNotNull(b.getContext().getVariables());
	}
}
