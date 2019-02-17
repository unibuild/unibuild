package net.unibld.server.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.VersionInfo;

public class WebAppVersionInfo {
	private static final Logger LOG=LoggerFactory.getLogger(WebAppVersionInfo.class);
	
	public static String getWarBuildNumber(ServletContext sc) {
		return getWarManifestProperty(sc,"Implementation-Build");
	}
	
	public static String getWarVersion(ServletContext sc) {
		return getWarManifestProperty(sc,"Implementation-Version");
	}
	
	private static String getWarManifestProperty(ServletContext application,String property) {
		try {
			InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(inputStream);
			return VersionInfo.getProperty(manifest, property);
		} catch (IOException e1) {
	        LOG.error("Failed to load manifest",e1);
	    }
		return null;
	}
	
	public static String getWarVersionLabel(ServletContext sc) {
		String ver = getWarVersion(sc);
		if (ver==null) {
			LOG.warn("Version not available, returning N/A...");
			return "N/A";
		}
		
		
		String buildNumber = getWarBuildNumber(sc);
		if (buildNumber==null||buildNumber.trim().length()==0) {
			return ver;
		}
		return String.format("%s.%s",ver,buildNumber);
	}
}
