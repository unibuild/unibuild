package net.unibld.client.desktop.preferences;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import net.unibld.core.util.DataFolder;

public class ClientDataFolder {
	public static File getClientDataFolder() {
		File df = DataFolder.getDataFolder();
		String path = FilenameUtils.concat(df.getAbsolutePath(), "client");
		File dir=new File(path);
		dir.mkdirs();
		return dir;
	}
}
