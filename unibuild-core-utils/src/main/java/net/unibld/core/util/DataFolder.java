package net.unibld.core.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

public class DataFolder {
	public static File getDataFolder() {
		String doc = PlatformHelper.detectDocumentsFolder();
		String path = FilenameUtils.concat(doc, "unibuild");
		File dir=new File(path);
		dir.mkdirs();
		return dir;
	}
	
	
}
