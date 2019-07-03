package net.unibld.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extract the global configuration of Unibuild from a properties file on the classpath, and deploys at an OS specific location,
 * such as /etc/unibld
 * @author andor
 *
 */
public class GlobalConfigExtractor {
	private static final String GLOBAL_CFG_PATH_LINUX = "/etc/unibld/config.properties";
	private static final Logger LOG=LoggerFactory.getLogger(GlobalConfigExtractor.class);
	
	
	private static void extractGlobalConfigClasspath(File f) throws IOException {
		String name=String.format("/config/global.%s.properties",PlatformHelper.isWindows()?"win":"linux");
		
		InputStream is = GlobalConfigExtractor.class.getResourceAsStream(name);
		if (is==null) {
			throw new IllegalStateException("Invalid GlobalConfig classpath");
		}
		FileUtils.copyInputStreamToFile(is, f);
	}
	

	/**
	 * Loads the global configuration from the OS specific designated path without variable substitution
	 * @param dir Directory of the target directory of the global config or null for the default
	 * @throws Exception If an error occurs loading global config
	 */
	public static void extract(String dir) {
		String path=dir!=null?FilenameUtils.concat(dir,"config.properties"):getDefaultConfigPath();
		File f=new File(path);
		if (!f.exists()) {
			if (f.getParentFile()!=null) {
				f.getParentFile().mkdirs();
			}
			try {
				extractGlobalConfigClasspath(f);
				LOG.info("Extracted global config to: {}",f.getAbsolutePath());
			} catch (IOException e) {
				LOG.error("Failed to extract global config to: "+f.getAbsolutePath(),e);
			}
			
		}
		
		
	}

	/**
	 * @return The default global config path
	 */
	public static String getDefaultConfigPath() {
		
		if (PlatformHelper.isLinux()) {
			return GLOBAL_CFG_PATH_LINUX;
		} else {
			String df = DataFolder.getDataFolder().getAbsolutePath();
			
			return FilenameUtils.concat(df, "config.properties");
		}
		
	}
	
}
