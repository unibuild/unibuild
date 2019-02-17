package net.unibld.core.build;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.var.el.ELVariableProvider;

import org.junit.Assert;
import org.junit.Test;

public class ELVariableTest {

	@Test
	public void testNormalStringVar() {
		ELVariableProvider elp=new ELVariableProvider();
		IBuildContextAttributeContainer c = new IBuildContextAttributeContainer() {
			
			@Override
			public String getBuildContextAttribute(String name) {
				if (name.equals("laci")) {
					return "true";
				}
				return null;
			}

			@Override
			public Map<String,String> getTaskContextAttributeMap() {
				return Collections.singletonMap("laci","true");
			}

			@Override
			public void setVariables(Map<String, String> userVars) {
				// TODO Auto-generated method stub
				
			}
		};
		String val = elp.substitute("${laci}", c);
		Assert.assertNotNull(val);
		Assert.assertEquals(val,"true");
		
		String val2 = elp.substitute("${not laci}", c);
		Assert.assertNotNull(val2);
		Assert.assertEquals(val2,"false");
	}
	
	@Test
	public void testEscapedStringVar() {
		ELVariableProvider elp=new ELVariableProvider();
		IBuildContextAttributeContainer c = new IBuildContextAttributeContainer() {
			
			@Override
			public String getBuildContextAttribute(String name) {
				if (name.equals("build.dir")) {
					return "c:/tmp";
				}
				return null;
			}

			@Override
			public Map<String,String> getTaskContextAttributeMap() {
				return Collections.singletonMap("build.dir","c:/tmp");
			}

			@Override
			public void setVariables(Map<String, String> userVars) {
				// TODO Auto-generated method stub
				
			}
		};
		String val = elp.substitute("${build.dir}", c);
		Assert.assertNotNull(val);
		Assert.assertEquals(val,"c:/tmp");
		
		
	}
	
	@Test
	public void testEscapedBooleanVar() {
		ELVariableProvider elp=new ELVariableProvider();
		IBuildContextAttributeContainer c = new IBuildContextAttributeContainer() {
			
			@Override
			public String getBuildContextAttribute(String name) {
				if (name.equals("build.enabled")) {
					return "true";
				}
				if (name.equals("build.client.enabled")) {
					return "false";
				}
				return null;
			}

			@Override
			public Map<String,String> getTaskContextAttributeMap() {
				HashMap<String, String> ret=new HashMap<String, String>();
				ret.put("build.enabled", "true");
				ret.put("build.client.enabled", "false");
				return ret;
			}

			@Override
			public void setVariables(Map<String, String> userVars) {
				// TODO Auto-generated method stub
				
			}
		};
		String val = elp.substitute("${build.enabled}", c);
		Assert.assertNotNull(val);
		Assert.assertEquals(val,"true");
		
		String val2 = elp.substitute("${build.client.enabled}", c);
		Assert.assertNotNull(val2);
		Assert.assertEquals(val2,"false");
		
		String val3 = elp.substitute("${build.enabled and not build.client.enabled}", c);
		Assert.assertNotNull(val3);
		Assert.assertEquals(val3,"true");
	}
	
	@Test
	public void testConstant() {
		
	}
}
