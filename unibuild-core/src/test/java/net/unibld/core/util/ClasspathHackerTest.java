package net.unibld.core.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import net.unibld.core.test.UnitTestContext;

import org.junit.Assert;
import org.junit.Test;

public class ClasspathHackerTest {
	@Test
	public void testAddJarToClassPath() throws IOException, URISyntaxException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String path=UnitTestContext.extractResource(getClass(), "/plugin/unibuild-svn-plugin.jar");
		ClasspathHacker.addFile(path);
		Constructor<?> cs = ClassLoader.getSystemClassLoader().loadClass("net.unibld.plugins.svn.SvnTaskRunner").getConstructor();
		Object instance = cs.newInstance();
		Assert.assertNotNull(instance);
	}
}
