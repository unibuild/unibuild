package net.unibld.client.desktop.preferences;

import java.io.File;

import net.unibld.core.util.GlobalConfigExtractor;
import net.unibld.core.util.PlatformHelper;

public class PreferencesDefaults {
	public static final String PREFERENCES_LOCATION_APPDATA="AppData folder";
	public static final String PREFERENCES_LOCATION_DOCUMENTS="Documents folder";
	public static final String PREFERENCES_LOCATION_OTHER="Other";
	
	public static final String DATASTORE_TYPE_SQLITE="SQLite";
	public static final String DATASTORE_TYPE_MYSQL="MySQL";
	
	public static String getDefaultGlobalConfigLocation() {
		String path = GlobalConfigExtractor.getDefaultConfigPath();
		File f=new File(path);
		return f.getParent();
	}

	public static String getDefaultDataFolder() {
		return ClientDataFolder.getClientDataFolder().getAbsolutePath();
	}

	public static Preferences getDefaultPreferences() {
		Preferences p=new Preferences();
		p.setDataFolder(getDefaultDataFolder());
		p.setGlobalConfigLocation(getDefaultGlobalConfigLocation());
		return p;
	}

	public static String[] getPreferencesLocationTypes() {
		if (PlatformHelper.isWindows()) {
			return new String[]{PREFERENCES_LOCATION_APPDATA,PREFERENCES_LOCATION_DOCUMENTS,PREFERENCES_LOCATION_OTHER};
		} else {
			return new String[]{PREFERENCES_LOCATION_DOCUMENTS,PREFERENCES_LOCATION_OTHER};
		}
	}

	public static String[] getDatastoreTypes() {
		return new String[]{DATASTORE_TYPE_SQLITE,DATASTORE_TYPE_MYSQL};
	}
}
