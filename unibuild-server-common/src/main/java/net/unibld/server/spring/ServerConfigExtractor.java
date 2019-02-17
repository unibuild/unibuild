package net.unibld.server.spring;

import java.io.File;
import java.io.InputStream;

import net.unibld.core.util.GlobalConfigExtractor;
import net.unibld.core.util.PlatformHelper;

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
public class ServerConfigExtractor {
	private static final Logger LOG=LoggerFactory.getLogger(ServerConfigExtractor.class);
	
	
	private static void extractGlobalConfigClasspath(File f) throws Exception {
		String name=String.format("/config/global.%s.properties",PlatformHelper.isWindows()?"win.server":"linux.server");
		
		InputStream is = ServerConfigExtractor.class.getResourceAsStream(name);
		if (is==null) {
			throw new IllegalStateException("Invalid GlobalConfig classpath");
		}
		FileUtils.copyInputStreamToFile(is, f);
	}
	

	/**
	 * Loads the global configuration from the OS specific designated path without variable substitution
	 * @return Global configuration object
	 * @throws Exception If an error occurs loading global config
	 */
	public static void extract(String dir) throws Exception {
		String path=dir!=null?FilenameUtils.concat(dir,"config.properties"):GlobalConfigExtractor.getDefaultConfigPath();
		File f=new File(path);
		if (!f.exists()) {
			if (f.getParentFile()!=null) {
				f.getParentFile().mkdirs();
			}
			try {
				extractGlobalConfigClasspath(f);
				LOG.info("Extracted global config to: {}",f.getAbsolutePath());
			} catch (Exception e) {
				LOG.error("Failed to extract global config to: "+f.getAbsolutePath(),e);
			}
			if (!f.exists()) {
				throw new IllegalStateException("Failed to extract global config to: "+f.getAbsolutePath());
			}
		}
		
		
	}

	
}
