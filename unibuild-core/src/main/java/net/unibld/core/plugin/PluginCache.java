package net.unibld.core.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.unibld.core.util.ClasspathHacker;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A Spring component that caches plugin jars located in the plugin directory.
 * @author andor
 *
 */
@Component
public class PluginCache {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginCache.class);

	@Autowired
	private PluginDetector pluginDetector;
	
	private String hash;

	private List<Plugin> plugins;
	
	public void writePluginCache(List<Plugin> plugins, String hash) throws IOException {
		this.hash=hash;
		this.plugins=plugins;
		
		File cacheFile=getCacheFile();
		LOGGER.info("Saving plugin cache to: {}...",cacheFile.getAbsolutePath());
		
		Properties p=new Properties();
		p.setProperty("hash", hash);
		for (Plugin plugin:plugins) {
			p.setProperty("plugin."+plugin.getName()+".package", plugin.getPackagePath());
			p.setProperty("plugin."+plugin.getName()+".jar", plugin.getPluginJarPath());
		}
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(cacheFile);
			p.store(fos, null);
		} finally {
			if (fos!=null) {
				fos.close();
			}
		}
	}

	public String getHash() {
		if (hash==null) {
			loadPluginCache();
		}
		return hash;
	}

	private void loadPluginCache() {
		if (!isExisting()) {
			throw new IllegalStateException("Plugin cache does not exist");
		}
		
		File cacheFile=getCacheFile();
		
		Properties p=new Properties();
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(cacheFile);
			p.load(fis);
		} catch (IOException ex) {
			LOGGER.error("Failed to read plugin cache",ex);
			throw new IllegalStateException("Failed to read plugin cache",ex);
		} finally {
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.error("Failed to close plugin cache input stream",e);
				}
			}
		}
		
		this.hash=p.getProperty("hash");
		
		this.plugins=new ArrayList<Plugin>();
		Set<Object> keys = p.keySet();
		for (Object key:keys) {
			String keyStr=(String) key;
			if (keyStr.startsWith("plugin.")&& keyStr.endsWith(".package")) {
				int endIdx = keyStr.lastIndexOf(".package");
				String name=keyStr.substring(7,endIdx);
				String packagePath=p.getProperty(keyStr);
				
				Plugin plugin=new Plugin();
				plugin.setName(name);
				plugin.setPackagePath(packagePath);
				plugin.setPluginJarPath(p.getProperty("plugin."+name+".jar"));
				if (plugin.getPluginJarPath()==null) {
					throw new IllegalStateException("Plugin jar path not found for plugin: "+name);
				}
				
				if (pluginDetector.isStandalone()) {
					try {
						ClasspathHacker.addFile(new File(plugin.getPluginJarPath()));
					} catch (IOException e) {
						LOGGER.error("Failed to add plugin to classpath",e);
						throw new IllegalStateException("Failed to add plugin to classpath",e);
					}
				}
				plugins.add(plugin);
				LOGGER.info("Loaded plugin from cache: {}",name);
			}
		}
	}

	public boolean isExisting() {
		File cacheFile=getCacheFile();
		return cacheFile.exists()&&cacheFile.isFile();
	}

	private File getCacheFile() {
		return new File(FilenameUtils.concat(pluginDetector.getPluginDir(), "plugin.cache"));
	}

	public List<Plugin> getPlugins() {
		if (plugins==null) {
			loadPluginCache();
		}
		return plugins;
	}

}
