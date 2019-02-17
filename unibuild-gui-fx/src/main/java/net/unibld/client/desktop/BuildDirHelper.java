package net.unibld.client.desktop;

import org.apache.commons.io.FilenameUtils;

import net.unibld.client.desktop.preferences.PreferencesManager;

public class BuildDirHelper {

	public static String getBuildPath(String projectName) {
		String buildsDir = FilenameUtils.concat(PreferencesManager.getInstance().getPreferences().getDataFolder(), "builds");
		String buildPath=FilenameUtils.concat(buildsDir, projectName);
		return buildPath;
	}

}
