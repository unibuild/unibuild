package net.unibld.core.plugin;

import java.io.IOException;
import java.net.URISyntaxException;

import net.unibld.core.test.UnitTestContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class PluginDetectorTest {

	@Autowired
	private PluginDetector detector;
	@Autowired
	private ApplicationContext context;
	
	@Test
	public void testDetectPlugins() throws URISyntaxException, IOException, ClassNotFoundException {
		UnitTestContext.extractResourceTo(getClass(), "/plugin/unibuild-svn-plugin.jar", detector.getPluginDir());
		
		detector.detectPlugins();
		
		Class<?> klazz = Class.forName("net.unibld.plugins.svn.SvnTaskRunner");
		Assert.assertNotNull(klazz);
		
		Object bean = context.getBean(klazz);
		Assert.assertNotNull(bean);
		
	}
}
