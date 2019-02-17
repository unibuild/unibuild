package net.unibld.core.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

public class PluginProjectCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginProjectCreator.class);

	public void createPluginProject(String dirPath,PluginDefinition pluginDefinition) {
		if (dirPath==null) {
			throw new IllegalArgumentException("Directory path was null");
		}
		
		File dir=new File(dirPath);
		if (!dir.exists()||!dir.isDirectory()) {
			throw new IllegalArgumentException("Invalid directory path: "+dirPath);
		}
		
		if (pluginDefinition==null) {
			throw new IllegalArgumentException("Plugin definition was null");
		}
		if (pluginDefinition.getName()==null) {
			throw new IllegalArgumentException("Plugin name was null");
		}
		
		if (pluginDefinition.getPackagePath()==null) {
			throw new IllegalArgumentException("Plugin package was null");
		}
		
		if (!pluginDefinition.isInternal() && pluginDefinition.getGroup()==null) {
			throw new IllegalArgumentException("External plugins must provide a Maven group");
		}
		
		
		File projectDir=new File(FilenameUtils.concat(dirPath, pluginDefinition.getName()));
		projectDir.mkdirs();
		LOGGER.info("Creating plugin project {} to: {} ...",pluginDefinition.getName(),projectDir.getAbsolutePath());
		
		URL url = getClass().getResource("/plugin/template");
		if (url == null) {
			throw new IllegalStateException("Project template resource folder not found");
		}
		
		try {
			File templateDir = new File(url.toURI());
			
			FileUtils.copyDirectory(templateDir, projectDir);
			LOGGER.info("Extracted plugin project template to: {}",projectDir.getAbsolutePath());
		} catch (Exception ex) {
			LOGGER.error("Failed to extract template",ex);
			throw new IllegalStateException("Failed to extract template",ex);
		}
		
		if (pluginDefinition.isInternal()) {
			moveFile(projectDir, "pom-internal.xml","pom.xml");
			deleteFile(projectDir, "pom-external.xml");
		} else {
			moveFile(projectDir, "pom-external.xml","pom.xml");
			deleteFile(projectDir, "pom-internal.xml");
			
		}
		
		
		editFile(projectDir,"pom.xml",pluginDefinition);
		editFile(projectDir,"src/main/resources/META-INF/plugin.properties",pluginDefinition,'$');
		editFile(projectDir,"src/test/resources/spring/test-context.xml",pluginDefinition);
		editFile(projectDir, "src/test/java/Test.template",pluginDefinition);
		
		String packagePath=pluginDefinition.getPackagePath().replace(".", "/");
		createDir(projectDir,"src/main/java/"+packagePath);
		createDir(projectDir,"src/test/java/"+packagePath);
		moveFile(projectDir, "src/test/java/Test.template",
				String.format("src/test/java/%s/%sTest.java",packagePath,pluginDefinition.getSimpleNameOrNameUpperClean()));
		moveFile(projectDir, "src/test/resources/config/test.properties", 
				String.format("src/test/resources/config/%s-test.properties",pluginDefinition.getSimpleNameOrName()));
		moveFile(projectDir, "src/test/resources/spring/test-context.xml", 
				String.format("src/test/resources/spring/%s-test-context.xml",pluginDefinition.getSimpleNameOrName()));
	}

	private void createDir(File projectDir, String relpath) {
		try {
			String path=FilenameUtils.concat(projectDir.getAbsolutePath(), relpath);
			LOGGER.info("Creating dir: {}...",path);
			File file=new File(path);
			file.mkdirs();
		} catch (Exception ex) {
			LOGGER.error("Failed to create dir: "+relpath,ex);
			throw new IllegalStateException("Failed to create dir: "+relpath,ex);
		}
	}

	private void editFile(File projectDir,String relpath, PluginDefinition pluginDefinition) {
		editFile(projectDir, relpath, pluginDefinition,'#');
	}
	private void editFile(File projectDir,String relpath, PluginDefinition pluginDefinition,char delimiter) {
		try {
			String path=FilenameUtils.concat(projectDir.getAbsolutePath(), relpath);
			LOGGER.info("Editing file with ST: {}...",path);
			File file=new File(path);
			String str=FileUtils.readFileToString(file, "UTF-8");
			ST st = new ST(str, delimiter, delimiter);
			st.add("plugin", pluginDefinition);
			
			String result=st.render();
			FileUtils.writeStringToFile(file, result, "UTF-8");
		} catch (IOException ex) {
			LOGGER.error("Failed to edit file: "+relpath,ex);
			throw new IllegalStateException("Failed to edit file: "+relpath,ex);
		}
	}
	private void moveFile(File projectDir,String relpath, String reltarget) {
		try {
			String path=FilenameUtils.concat(projectDir.getAbsolutePath(), relpath);
			String target=FilenameUtils.concat(projectDir.getAbsolutePath(), reltarget);
			LOGGER.info("Moving file: {} -> {}...",path,target);
			File file=new File(path);
			File targetFile=new File(target);
			if (targetFile.exists()&&targetFile.isFile()) {
				targetFile.delete();
			}
			FileUtils.moveFile(file, targetFile);
		} catch (IOException ex) {
			LOGGER.error("Failed to move file: "+relpath,ex);
			throw new IllegalStateException("Failed to move file: "+relpath,ex);
		}
	}
	
	private void deleteFile(File projectDir,String relpath) {
		try {
			String path=FilenameUtils.concat(projectDir.getAbsolutePath(), relpath);
			LOGGER.info("Deleting file: {} -> {}...",path);
			File file=new File(path);
			file.delete();
		} catch (Exception ex) {
			LOGGER.error("Failed to delete file: "+relpath,ex);
			throw new IllegalStateException("Failed to delete file: "+relpath,ex);
		}
	}
}
