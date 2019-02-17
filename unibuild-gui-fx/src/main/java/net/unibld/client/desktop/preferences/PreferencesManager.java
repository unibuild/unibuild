package net.unibld.client.desktop.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.client.desktop.SpringBeanFactory;
import net.unibld.core.config.global.GlobalConfig;

public class PreferencesManager {
	private static final Logger LOGGER=LoggerFactory.getLogger(PreferencesManager.class);
	
	private static final DateFormat DF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static PreferencesManager inst;
	
	public static PreferencesManager getInstance() {
		if (inst==null) {
			inst=new PreferencesManager();
			inst.init();
		}
		return inst;
	}
	private Properties properties;

	private Preferences preferences;
	private void init() {
		properties=new Properties();
		
		//load preferences first if existing
		if (isPreferencesExisting()) {
			FileInputStream fis=null;
			try {
				fis=new FileInputStream(getPreferencesPath());
				properties.load(fis);
				LOGGER.info("Preferences successfully loaded.");
				
				preferences=new Preferences();
				preferences.load(properties);
				
				
			} catch (IOException ex) {
				LOGGER.error("Failed to load preferences",ex);
				throw new IllegalStateException("Failed to load preferences",ex);
			} finally {
				if (fis!=null) {
					try {
						fis.close();
					} catch (IOException e) {
						LOGGER.error("Failed to close preferences input stream",e);
					}
				}
			}
		} else {
			preferences=PreferencesDefaults.getDefaultPreferences();
		}
		
		//load global Unibuild config
		try {
			GlobalConfig cfg = SpringBeanFactory.getBean(GlobalConfig.class);
			preferences.load(cfg);
		} catch (Exception ex) {
			LOGGER.error("Failed to load global config",ex);
			throw new IllegalStateException("Failed to load global config",ex);
		} 
	}
	public String getPreferencesPath() {
		File df = ClientDataFolder.getClientDataFolder();
		String path = FilenameUtils.concat(df.getAbsolutePath(), "locator.properties");
		File locatorFile=new File(path);
		if (!locatorFile.exists()) {
			return getDefaultPreferencesPath(df);
		}
		
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(locatorFile);
			Properties p=new Properties();
			p.load(fis);
			
			if (!p.containsKey("preferences.location")) {
				return getDefaultPreferencesPath(df);
			}
			String location = p.getProperty("preferences.location");
			if (StringUtils.isEmpty(location)) {
				LOGGER.info("Preferences location was not specified");
				return getDefaultPreferencesPath(df);
			}
			
			File prefsFile=new File(location);
			if (!prefsFile.exists()||!prefsFile.isFile()) {
				LOGGER.warn("Invalid preferences location: "+location);
				return getDefaultPreferencesPath(df);
			}
			return prefsFile.getAbsolutePath();
		} catch (IOException ex) {
			LOGGER.error("Failed to load locator.properties",ex);
			return getDefaultPreferencesPath(df);
		} finally {
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.error("Failed to close locator.properties input stream",e);
				}
			}
		}
	}
	protected String getDefaultPreferencesPath(File df) {
		return FilenameUtils.concat(df.getAbsolutePath(), "preferences.properties");
	}
	public boolean isPreferencesExisting() {
		String path = getPreferencesPath();
		if (path==null) {
			return false;
		}
		File f=new File(path);
		return f.exists()&&f.isFile();
	}
	/*public String getDataFolder() {
		return properties.getProperty("general.data.folder", ClientDataFolder.getClientDataFolder().getAbsolutePath());
	}
	public String getGlobalConfigPath() {
		return properties.getProperty("general.global.config.path", new GlobalConfigLoader().getDefaultConfigPath());
	}*/
	public Preferences getPreferences() {
		return preferences;
	}
	public void save(Preferences p) throws IOException {
		if (p==null) {
			throw new IllegalArgumentException("Preferences was null");
		}
		mergePreferences(p);
		
		Properties props=new Properties();
		preferences.exportTo(props);
		
		String preferencesPath = getPreferencesPath();
		
		saveProperties(preferencesPath, props, "Preferences saved: "+DF.format(new Date()));
	}
	private void mergePreferences(Preferences p) {
		if (p!=null) {
			preferences.merge(p);
		}
	}
	public void savePreferencesLocation(String location) throws IOException {
		if (location==null) {
			throw new IllegalArgumentException("Location was null");
		}
		File df = ClientDataFolder.getClientDataFolder();
		String path = FilenameUtils.concat(df.getAbsolutePath(), "locator.properties");
		File locatorFile=new File(path);
		if (!locatorFile.exists()) {
			String defPath = getDefaultPreferencesPath(df);
			if (!location.equals(defPath)) {
				Properties p=new Properties();
				saveProperties(locatorFile.getAbsolutePath(),p,getLocatorPropertiesComments());
				LOGGER.info("Locator properties successfully saved to: "+locatorFile.getAbsolutePath());
			}
		} else {
			//load and check first
			FileInputStream fis=null;
			String stored=null;
			Properties p=new Properties();
			
			try {
				fis=new FileInputStream(locatorFile);
				p.load(fis);
				
				stored = p.getProperty("preferences.location");
				
			} catch (IOException ex) {
				LOGGER.error("Failed to load locator.properties",ex);
			} finally {
				if (fis!=null) {
					try {
						fis.close();
					} catch (IOException e) {
						LOGGER.error("Failed to close locator.properties input stream",e);
					}
				}
			}
			if (stored==null||!stored.equals(location)) {
				p.put("preferences.location", location);
				saveProperties(locatorFile.getAbsolutePath(),p,getLocatorPropertiesComments());
				LOGGER.info("Locator properties successfully saved to: "+locatorFile.getAbsolutePath());
			}
		}
	}

	private String getLocatorPropertiesComments() {
		return String.format("locator.properties saved %s", DF.format(new Date()));
	}
	private void saveProperties(String path, Properties p,String comment) throws IOException {
		FileOutputStream fos = null;
		try {
			fos=new FileOutputStream(path);
			p.store(new OutputStreamWriter(
			    fos, "UTF-8"), 
			    comment);
		} catch (IOException ex) {
			LOGGER.error("Failed to save properties",ex);
			throw ex;
		} finally {
			if (fos!=null) {
				fos.close();
			}
		}
	}
	public boolean checkPreferenceChanged(String stored, String value) {
		if (stored==null) {
			return !StringUtils.isEmpty(value);
		}
		return !stored.equals(value);
	}

	
}
