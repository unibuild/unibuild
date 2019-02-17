package net.unibld.client.desktop.preferences;

import java.util.Properties;

import net.unibld.core.config.global.GlobalConfig;
import net.unibld.core.persistence.DriverClassNames;

public class Preferences {
	//general
	private String globalConfigLocation;
	private String dataFolder;
	
	//db
	private String datastoreLocation;
	private String datastoreType;
	private String datastoreUser;
	private String datastorePass;
	
	//Maven
	private String m2Home;
	
	//Android
	private String androidSdkDir;
	
	//Unity
	private String unityDir;

	public String getGlobalConfigLocation() {
		return globalConfigLocation;
	}

	public void setGlobalConfigLocation(String globalConfigLocation) {
		this.globalConfigLocation = globalConfigLocation;
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public void setDataFolder(String dataFolder) {
		this.dataFolder = dataFolder;
	}

	public String getDatastoreLocation() {
		return datastoreLocation;
	}

	public void setDatastoreLocation(String datastoreLocation) {
		this.datastoreLocation = datastoreLocation;
	}

	public String getDatastoreType() {
		return datastoreType;
	}

	public void setDatastoreType(String datastoreType) {
		this.datastoreType = datastoreType;
	}

	public String getDatastoreUser() {
		return datastoreUser;
	}

	public void setDatastoreUser(String datastoreUser) {
		this.datastoreUser = datastoreUser;
	}

	public String getDatastorePass() {
		return datastorePass;
	}

	public void setDatastorePass(String datastorePass) {
		this.datastorePass = datastorePass;
	}

	public String getM2Home() {
		return m2Home;
	}

	public void setM2Home(String m2Home) {
		this.m2Home = m2Home;
	}

	public String getAndroidSdkDir() {
		return androidSdkDir;
	}

	public void setAndroidSdkDir(String androidSdkDir) {
		this.androidSdkDir = androidSdkDir;
	}

	public String getUnityDir() {
		return unityDir;
	}

	public void setUnityDir(String unityDir) {
		this.unityDir = unityDir;
	}

	public void load(Properties properties) {
		if (properties!=null) {
			dataFolder=properties.getProperty("general.data.folder", PreferencesDefaults.getDefaultDataFolder());
			globalConfigLocation=properties.getProperty("general.global.config.path", PreferencesDefaults.getDefaultGlobalConfigLocation());
			
			
		} else {
			dataFolder=PreferencesDefaults.getDefaultDataFolder();
			globalConfigLocation=PreferencesDefaults.getDefaultGlobalConfigLocation();
		}
	}
	
	public void exportTo(Properties p) {
		if (p==null) {
			throw new IllegalArgumentException("Properties was null");
		}
		p.put("general.data.folder", dataFolder);
		p.put("general.global.config.path", globalConfigLocation);
	}
	/*public void exportTo(GlobalConfig cfg) {
		if (cfg==null) {
			throw new IllegalArgumentException("GlobalConfig was null");
		}
		
		if (cfg.getPersistenceConfig()==null) {
			cfg.setPersistenceConfig(new PersistenceConfig());
		}
		cfg.getPersistenceConfig().setUrl(datastoreLocation);
		cfg.getPersistenceConfig().setUser(datastoreUser);
		cfg.getPersistenceConfig().setPassword(datastorePass);
		cfg.getPersistenceConfig().setDriverClass(toDriverClass(datastoreType));
		
	}*/
	
	private String toDriverClass(String type) {
		if (type==null||type.equals(PreferencesDefaults.DATASTORE_TYPE_SQLITE)) {
			return null;
		}
		if (type.equals(PreferencesDefaults.DATASTORE_TYPE_MYSQL)) {
			return DriverClassNames.DRIVER_CLASS_MYSQL;
		}
		
		throw new IllegalArgumentException("Invalid datastore type: "+type);
	}

	public void load(GlobalConfig cfg) {
		if (cfg!=null) {
			datastoreLocation=cfg.getPropertyAsString("persistence.jdbc.url");
				datastoreUser=cfg.getPropertyAsString("persistence.jdbc.user");
				datastorePass=cfg.getPropertyAsString("persistence.jdbc.password");
				
				datastoreType=parseDatastoreTypeFromDriverClass(cfg.getPropertyAsString("persistence.jdbc.driver"));
			
		}
	}

	private String parseDatastoreTypeFromDriverClass(String driverClass) {
		if (driverClass==null||driverClass.equals(DriverClassNames.DRIVER_CLASS_SQLITE)) {
			//if no driver specified, it must be built-in SQLite
			return PreferencesDefaults.DATASTORE_TYPE_SQLITE;
		}
		if (driverClass.equals(DriverClassNames.DRIVER_CLASS_MYSQL)) {
			return PreferencesDefaults.DATASTORE_TYPE_MYSQL;
		}
		
		throw new IllegalArgumentException("Invalid driver class: "+driverClass);
	}

	public void merge(Preferences p) {
		if (p!=null) {
			androidSdkDir=p.androidSdkDir;
			dataFolder=p.dataFolder;
			datastoreLocation=p.datastoreLocation;
			datastorePass=p.datastorePass;
			datastoreType=p.datastoreType;
			datastoreUser=p.datastoreUser;
			
			globalConfigLocation=p.globalConfigLocation;
			m2Home=p.m2Home;
			unityDir=p.unityDir;
			
		}
	}

	
}
