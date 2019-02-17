package net.unibld.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for reading installed application version.
 * @author andor
 *
 */
public class VersionInfo {
	private static final Logger LOG=LoggerFactory.getLogger(VersionInfo.class);
	public static String getJarVersion() {
		return VersionInfo.class.getPackage().getImplementationVersion();
	}
	public static String getJarBuildNumber() {
		return getJarManifestProperty("Implementation-Build");
	}
	
	private static String getJarManifestProperty(String property) {
		 Enumeration resEnum;
		    try {
		        resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
		        while (resEnum.hasMoreElements()) {
		            try {
		                URL url = (URL)resEnum.nextElement();
		                InputStream is = url.openStream();
	                    Manifest manifest = new Manifest(is);
	                    return getProperty(manifest, property);
	                
		            }
		            catch (Exception e) {
		                LOG.error("Failed to read manifest elements",e);
		            }
		        }
		    } catch (IOException e1) {
		        LOG.error("Failed to load manifest",e1);
		    }
		    return null; 
	}
	
	public static String getProperty(Manifest manifest, String property) {
		Attributes mainAttribs = manifest.getMainAttributes();
		String version = mainAttribs.getValue(property);
		if(version != null) {
		    return version;
		} else {
			LOG.warn("Manifest property not found: {}",property);
			return null;
		}
	}
	public static String getJarVersionLabel() {
		String ver = getJarVersion();
		if (ver==null) {
			LOG.warn("Version not available, returning N/A...");
			return "N/A";
		}
		
		
		String buildNumber = VersionInfo.getJarBuildNumber();
		if (buildNumber==null||buildNumber.trim().length()==0) {
			return ver;
		}
		return String.format("%s.%s",VersionInfo.getJarVersion(),buildNumber);
	}
	
	
	
	
} 