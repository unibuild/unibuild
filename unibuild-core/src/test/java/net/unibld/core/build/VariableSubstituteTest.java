package net.unibld.core.build;

import java.util.UUID;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.var.VariableSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class VariableSubstituteTest {
	@Autowired
	private VariableSupport support;
	@Autowired
	private BuildToolContextHolder contextHolder;
	@Test
	public void testOneMatch() {
		BuildToolContext context = contextHolder.createContext(UUID.randomUUID().toString());
		context.setVariables(new HashMap<>());
		context.getVariables().put("scm.branch","trunk");
		String str=support.substitute("http://ewise.hu/svn/unibuild/${scm.branch}", context);
		Assert.assertNotNull(str);
		Assert.assertTrue(!str.contains("${"));
		Assert.assertTrue(str.contains("trunk"));
	}
	
	@Test
	public void testNoMatches() {
		BuildToolContext context = contextHolder.createContext(UUID.randomUUID().toString());
		context.setVariables(new HashMap<>());
		
		context.getVariables().put("scm.branch","trunk");
		
		String str=support.substitute("http://ewise.hu/svn/unibuild", context);
		Assert.assertNotNull(str);
		Assert.assertTrue(!str.contains("${"));
		Assert.assertEquals(str,"http://ewise.hu/svn/unibuild");
	}
	@Test(expected=BuildException.class)
	public void testUndefinedVar() {
		BuildToolContext context = contextHolder.createContext(UUID.randomUUID().toString());
		context.setVariables(new HashMap<>());
		
		context.getVariables().put("scm.branch","trunk");
		
		support.substitute("http://ewise.hu/svn/unibuild/${scm.branch}/laci/${instance.name}/${scm.branch}", context);
		
	}
	@Test
	public void testMultiMatches() {
		BuildToolContext context = contextHolder.createContext(UUID.randomUUID().toString());
		context.setVariables(new HashMap<>());
		
		context.getVariables().put("scm.branch","trunk");
		context.getVariables().put("instance.name","live");
		
		String str=support.substitute("http://ewise.hu/svn/unibuild/${scm.branch}/laci/${instance.name}/${scm.branch}", context);
		Assert.assertNotNull(str);
		Assert.assertTrue(!str.contains("${"));
		Assert.assertTrue(str.contains("unibuild/trunk"));
		Assert.assertTrue(str.contains("live/trunk"));
	}
}
