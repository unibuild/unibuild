package net.unibld.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import net.unibld.core.build.BuildException;


public class BuildNumberUtils {
	private static final String DEFAULT_BUILD_NUMBER_PROP = "buildNumber";

	public static String getBuildNumberFromProperties(Properties p,String propName) {
		if (!StringUtils.isEmpty(propName)) {
			if (!p.containsKey(propName.trim())) {
				throw new IllegalArgumentException("Invalid build number property name: "+propName);
			}
			return p.getProperty(propName.trim()).trim();
		} else {
			if (!p.containsKey(DEFAULT_BUILD_NUMBER_PROP)) {
				throw new IllegalArgumentException("Default build number propery not found: buildNumber");
			}
			return p.getProperty(DEFAULT_BUILD_NUMBER_PROP).trim();
		}
	}

	public static void incBuildNumberInProperties(Properties p,
			String propName) {
		String str = getBuildNumberFromProperties(p, propName);
		if (StringUtils.isEmpty(str)) {
			setBuildNumberInProperties(p,propName,1);
		} else {
			int curr=Integer.parseInt(str);
			setBuildNumberInProperties(p,propName,curr+1);
		}
	}

	public static void setBuildNumberInProperties(Properties p,
			String propName, int val) {
		if (!StringUtils.isEmpty(propName)) {
			p.setProperty(propName.trim(),String.valueOf(val));
		} else {
			p.setProperty(DEFAULT_BUILD_NUMBER_PROP,String.valueOf(val));
		}
	}
	
	public static String getBuildNumberFromProperties(String buildNumberProperties,String propName) {
		Properties p=new Properties();
		File f=new File(buildNumberProperties);
		if (!f.exists()||!f.isFile()) {
			throw new IllegalArgumentException("Invalid build number properties file: "+buildNumberProperties);
		}
		
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(f);
			p.load(fis);
			
			return BuildNumberUtils.getBuildNumberFromProperties(p,propName);
		} catch (IOException ex) {
			//logError("Failed to read build number properties file: "+buildNumberProperties, ex);
			throw new BuildException("Failed to read build number properties file: "+buildNumberProperties, ex);
		} finally {
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new BuildException("Failed to close build number input stream: "+buildNumberProperties, e);
				}
			}
		}
		
	}

}
