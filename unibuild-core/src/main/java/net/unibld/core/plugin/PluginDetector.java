package net.unibld.core.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.unibld.core.task.TaskRegistry;
import net.unibld.core.util.ClasspathHacker;
import net.unibld.core.util.DirectoryHash;
import net.unibld.core.util.Zip;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * A Spring component that detects available plugins in the plugin.dir
 * @author andor
 *
 */
@Component
public class PluginDetector {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginDetector.class);

	@Value("${plugin.dir}")
	private String pluginDir;
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private PluginRegistry pluginRegistry;
	@Autowired
	private PluginCache pluginCache;
	@Autowired
	private TaskRegistry taskRegistry;
	
	/**
	 * @return True if the application is a standalone one
	 */
	public boolean isStandalone() {
		return !(applicationContext.getClass().getSimpleName().equals("XmlWebApplicationContext"));
	}
	
	/**
	 * Detects available plugins in the plugin.dir. Plugins are cached in a file using an MD5 hash
	 * of the plugin folder. If the hash changes, the cache is ignored.
	 */
	public void detectPlugins() {
		LOGGER.info("Detecting plugins in: {}...",pluginDir);
		File dir = new File(pluginDir);
		
		if (pluginCache.isExisting() && pluginCache.getHash().equals(calculateDirHash(dir))) {
			long start=System.currentTimeMillis();
			
			List<Plugin> plugins = pluginCache.getPlugins();
			for (Plugin p:plugins) {
				try {
					processPlugin(p);
					LOGGER.info("Plugin loaded successfully from cache: {}",p.getName());
				} catch (Exception ex) {
					LOGGER.error("Plugin failed to load from cache: "+p.getName(),ex);
				}
			}
			long end=System.currentTimeMillis();
			
			LOGGER.info("Loaded {} plugins from cache in {} ms.",plugins.size(),end-start);
		} else {
			long start=System.currentTimeMillis();
			LOGGER.info("Scanning for plugins in: {}...",pluginDir);
			File[] files=findJarFilesInPluginDir();
			LOGGER.info("{} jar files found in: {}",files!=null ? files.length : 0,pluginDir);
			
			int i=0;
			List<Plugin> plugins=new ArrayList<Plugin>();
			if (files!=null) {
				
				for (File f:files) {
					LOGGER.info("Processing {}...",f.getAbsolutePath());
					Plugin plugin=processJarFile(f);
					if (plugin!=null) {
						i++;
						plugins.add(plugin);
					}
				}
			}
			
			try {
				pluginCache.writePluginCache(plugins,calculateDirHash(dir));
			} catch (IOException e) {
				LOGGER.error("Failed to write plugin cache",e);
			}
			
			long end=System.currentTimeMillis();
			LOGGER.info("Scanning for plugins completed in {} ms, {} plugins found.",end-start,i);
		}
	}

	private String calculateDirHash(File dir) {
		return DirectoryHash.calcMD5HashForDir(dir, Arrays.asList("tmp","plugin.cache"));
	}

	private Plugin processJarFile(File f) {
		if (!f.exists()||!f.isFile()) {
			throw new IllegalArgumentException("Invalid file: "+f.getAbsolutePath());
		}
		
		File tempDir=new File(FilenameUtils.concat(pluginDir, "tmp/"+f.getName()));
		
		try {
			tempDir.mkdirs();
			
			Zip.unzip(f.getAbsolutePath(), tempDir.getAbsolutePath());
		} catch (Exception ex){
			LOGGER.error("Failed to unzip file: "+f.getAbsolutePath(),ex);
			cleanupTempDir(tempDir);
			return null;
		}
		
		String propsPath=FilenameUtils.concat(tempDir.getAbsolutePath(),"META-INF/plugin.properties");
		File propsFile=new File(propsPath);
		if (!propsFile.exists()||!propsFile.isFile()) {
			LOGGER.info("Not a plugin jar: {} (plugins.properties not found in META-INF)",f.getName());
			cleanupTempDir(tempDir);
			return null;
		}
		
		Properties props=new Properties();
		
		FileInputStream fis=null;
		boolean propertiesError=false;
		try {
			fis=new FileInputStream(propsFile);
			props.load(fis);
		} catch (IOException ex) {
			LOGGER.error("Failed to load properties file",ex);
			propertiesError=true;
		} finally {
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.error("Failed to close properties stream",e);
				}
			}
		}
		
		if (propertiesError) {
			LOGGER.info("Not a plugin jar: {} (could not load plugins.properties from META-INF)",f.getName());
			cleanupTempDir(tempDir);
			return null;
		}
		
		String name = props.getProperty("plugin.name");
		String packagePath = props.getProperty("plugin.package");
		
		if (StringUtils.isEmpty(name)) {
			LOGGER.error("Invalid plugin jar: {} (could not find plugin.name in plugins.properties)",f.getName());
			cleanupTempDir(tempDir);
			return null;
		}
		
		if (StringUtils.isEmpty(packagePath)) {
			LOGGER.error("Invalid plugin jar: {} (could not find plugin.package in plugins.properties)",f.getName());
			cleanupTempDir(tempDir);
			return null;
		}
		if (packagePath.startsWith("net.unibld.core")) {
			LOGGER.error("Invalid plugin jar: {} (invalid plugin.package in plugins.properties: net.unibld.core)",f.getName());
			cleanupTempDir(tempDir);
			return null;
		}
		
		LOGGER.info("Plugin found: name={}, package={}, jar={}",name,packagePath,f.getName());
		cleanupTempDir(tempDir);
		
		try {
			Plugin plugin=createPlugin(name,packagePath,f,isStandalone());
			processPlugin(plugin);
			LOGGER.info("Plugin loaded successfully: {}",name);
			return plugin;
		} catch (Exception ex) {
			LOGGER.error("Plugin failed to load: "+name,ex);
			return null;
		}
		
		
	}

	private void processPlugin(Plugin plugin) {
		
		LOGGER.debug("Processing plugin: {} [{}]...",plugin.getName(),plugin.getPackagePath());
		if (pluginRegistry.isPluginExisting(plugin.getName())) {
			throw new IllegalStateException("Plugin already exists: "+plugin.getName());
		}
		
		pluginRegistry.addPlugin(plugin);
		
		AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
	    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
	    ClassPathBeanDefinitionScanner scanner=new ClassPathBeanDefinitionScanner(registry);
	
	    
		
		int found=scanner.scan(plugin.getPackagePath());
		LOGGER.info("Found {} Spring beans for plugin: {}",found,plugin.getName());
		
		int tasks=taskRegistry.introspect(plugin.getPackagePath());
		LOGGER.info("Found {} tasks for plugin: {}",tasks,plugin.getName());
		
		
	}

	private Plugin createPlugin(String name, String packagePath,File pluginJar,boolean addToClasspath) throws IOException {
		Plugin p=new Plugin();
		p.setName(name);
		p.setPackagePath(packagePath);
		p.setPluginJarPath(pluginJar.getAbsolutePath());
		if (addToClasspath) {
			ClasspathHacker.addFile(pluginJar);
		}
		return p;
	}

	private void cleanupTempDir(File tempDir) {
		LOGGER.info("Deleting temp dir: {}...",tempDir.getAbsolutePath());
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			LOGGER.error("Failed to delete temp dir: "+tempDir.getAbsolutePath(),e);
		}
	}

	private File[] findJarFilesInPluginDir() {
		File dir = new File(pluginDir);
		
		if (!dir.exists()) {
			throw new IllegalStateException("Plugin dir does not exist: "+pluginDir);
		}
		
		File[] matchingFiles = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".jar");
		    }
		});
		return matchingFiles;
	}

	/**
	 * @return Plugin folder
	 */
	public String getPluginDir() {
		return pluginDir;
	}

	
}
