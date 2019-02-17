package net.unibld.core.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class PluginProjectCreatorTest {
	@Test
	public void testCreatePlugin() throws IOException {
		createPlugin("test-plugin",null,"Test plugin","com.example.plugins",
				"com.example.plugins.testplugin",false);
	}

	private void createPlugin(String name,String simpleName,String desc,String group,String packagePath,boolean internal) throws IOException {
		File outDir=new File("./target/plugins-created");
		File projDir=new File("./target/plugins-created/"+name);
		if (projDir.exists()&&projDir.isDirectory()) {
			FileUtils.deleteDirectory(projDir);
		}
		outDir.mkdirs();
		
		PluginProjectCreator creator=new PluginProjectCreator();
		PluginDefinition pluginDefinition=new PluginDefinition();
		pluginDefinition.setName(name);
		pluginDefinition.setSimpleName(simpleName);
		pluginDefinition.setGroup(group);
		pluginDefinition.setDescription(desc);
		pluginDefinition.setPackagePath(packagePath);
		pluginDefinition.setInternal(internal);
		creator.createPluginProject("./target/plugins-created", pluginDefinition);
	}
	
	@Test
	public void testCreateDefaultPlugins() throws IOException {
		createPlugin("unibuid-cvs-plugin", "cvs", "UniBuild CVS plugin",
				"", "net.unibld.plugins.cvs",true);
		createPlugin("unibuid-android-plugin", "android", "UniBuild Android plugin",
				"", "net.unibld.plugins.android",true);
		createPlugin("unibuid-unity-plugin", "unity", "UniBuild Unity3D plugin",
				"", "net.unibld.plugins.unity",true);
		createPlugin("unibuid-innosetup-plugin", "innosetup", "UniBuild InnoSetup plugin",
				"", "net.unibld.plugins.innosetup",true);
		createPlugin("unibuid-tomcat-plugin", "tomcat", "UniBuild Tomcat server plugin",
				"", "net.unibld.plugins.tomcat",true);
	}
}
