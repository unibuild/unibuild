package net.unibld.core.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class that detects OS and other environmental settings, depending on the installation platform
 * @author andor
 *
 */
public class PlatformHelper {
	
	private static Logger LOG=LoggerFactory.getLogger(PlatformHelper.class);
	/**
	 * Detects the documents folder of the current user on various OS-s
	 * @return The documents folder of the current user
	 */
	public static String detectDocumentsFolder() {
		String doc=null;
		if (isMac()) {
			doc=String.format("/Users/%s/Documents",getLoggedOnUserName());
			LOG.info("Detected documents folder for Mac: "+doc);
			return doc;
		} else if (isUnix()) {
			doc=String.format("/home/%s",getLoggedOnUserName());
			LOG.info("Detected documents folder for Unix: "+doc);
			return doc;
		} else if (isWindows()) {
			if (isWindows7()||isWindows8()||isWindows10()) {
				doc=String.format("c:\\Users\\%s\\Documents",getLoggedOnUserName());
				LOG.info("Detected documents folder for Win7 or later: "+doc);
			} else {
				doc=String.format("C:\\Documents and Settings\\%s\\My Documents",getLoggedOnUserName());
				LOG.info("Detected documents folder for WinXP or earlier: "+doc);
			}
			return doc;
		}
		
		throw new IllegalArgumentException("OS is not supported: "+getOSName());
	}
	
	/**
	 * Returns the path of AppData/Local for the current user on Windows
	 * @return The path of AppData/Local for the current user on Windows
	 */
	public static String detectLocalAppDataFolder() {
		if (!isWindows()) {
			throw new IllegalStateException("AppData folder only exists on Windows");
		}
		
		return String.format("c:\\Users\\%s\\AppData\\Local",getLoggedOnUserName());
	}
	
	/**
	 * Returns the path of AppData/Roaming for the current user on Windows
	 * @return The path of AppData/Roaming for the current user on Windows
	 */
	public static String detectRoamingAppDataFolder() {
		if (!isWindows()) {
			throw new IllegalStateException("AppData folder only exists on Windows");
		}
		
		return String.format("c:\\Users\\%s\\AppData\\Roaming",getLoggedOnUserName());
	}
	/**
	 * @return User home directory (based on Java System property user.home)
	 */
	public static String getUserHome() {
		return System.getProperty("user.home");
	}
	/**
	 * @return Current directory (based on Java System property user.dir)
	 */
	public static String getCurrentDir() {
		return System.getProperty("user.dir");
	}
	
	/**
	 * @return True if the current system is a Microsoft Windows 7 (6.1)
	 */
	public static boolean isWindows7() {
	    String osName = System.getProperty("os.name");
	    String osVersion = getOsVersion();
	    return "Windows 7".equals(osName) && "6.1".equals(osVersion);
	}
	/**
	 * @return True if the current system is a Microsoft Windows 7 (6.1)
	 */
	public static boolean isWindows8() {
	    String osName = System.getProperty("os.name");
	    String osVersion = getOsVersion();
	    return "Windows 8".equals(osName) && "6.2".equals(osVersion);
	}
	/**
	 * @return True if the current system is a Microsoft Windows 7 (6.1)
	 */
	public static boolean isWindows10() {
	    String osName = System.getProperty("os.name");
	    return "Windows 10".equals(osName);
	}
	/**
	 * @return The name of the OS logged-on user (from user.name system property)
	 */
	public static String getLoggedOnUserName() {
		return System.getProperty("user.name");
	}
	
	/**
	 * @return The name of the current OS
	 */
	public static String getOSName() {
		return System.getProperty("os.name");
	}
	/**
	 * @return True if the current Java VM is 64 bit
	 */
	public static boolean is64Bit() {
		return "64".equals(System.getProperty("sun.arch.data.model"));
	}
	
	/**
	 * @return True if the current system's architecture is 64 bit
	 */
	public static boolean is64BitOs() {
		String arch = getOSArchitecture();
		return arch.equals("x86_64") || arch.equals("amd64") || arch.equals("ia64n");
	}
	/**
	 * @return The current system's architecture
	 */
	public static String getOSArchitecture() {
		return System.getProperty("os.arch").toLowerCase();
	}
	
	/**
	 * @return True if the current system is a Microsoft Windows
	 */
	public static boolean isWindows() {
		 
		return (getOSName().toLowerCase().indexOf("win") >= 0);
 
	}
 
	/**
	 * @return True if the current system is a MacOS/MacOSX
	 */
	public static boolean isMac() {
 
		return (getOSName().toLowerCase().indexOf("mac") >= 0);
 
	}
 
	/**
	 * @return True if the current system is a Unix-based system
	 */
	public static boolean isUnix() {
 
		return (getOSName().toLowerCase().indexOf("nix") >= 0 || getOSName().toLowerCase().indexOf("nux") >= 0 || 
				getOSName().toLowerCase().indexOf("aix") > 0 );
 
	}
 
	/**
	 * @return True if the current system is a Solaris
	 */
	public static boolean isSolaris() {
 
		return (getOSName().toLowerCase().indexOf("sunos") >= 0);
 
	}
	/**
	 * Writes the state of the current environment, such as JRE
	 * properties, system properties or environment variables, to a trace
	 * file name sys.env in the folder specified.
	 * @param tmpDir Folder to write the trace file to
	 * 
	 */
	public static void traceEnvironment(String tmpDir) {
		String str = getEnvironmentTrace();
		File f = new File(FilenameUtils.concat(tmpDir,"sys.env"));
		try {
			FileUtils.write(f, str, "UTF-8");
		} catch (IOException e) {
			LOG.error("Failed to trace system environment",e);
		}
	}
	/**
	 * Returns a trace string of the current environment, such as JRE
	 * properties, system properties or environment variables.
	 * @return Trace string of the current environment
	 */
	public static String getEnvironmentTrace() {
		StringBuilder b=new StringBuilder();
		b.append("\nTracing runtime environment:\n\n");
		b.append("OS name: "+getOSName());
		b.append("\n");
		b.append("OS version: "+System.getProperty("os.version"));
		b.append("\n");
		b.append("OS arch: "+System.getProperty("os.arch"));
		b.append("\n");
		b.append("JVM name: "+System.getProperty("java.vm.name"));
		b.append("\n");
		b.append("JVM version: "+System.getProperty("java.vm.version"));
		b.append("\n");
		b.append("JRE name: "+System.getProperty("java.runtime.name"));
		b.append("\n");
		b.append("JRE version: "+System.getProperty("java.runtime.version"));
		b.append("\n");
		b.append("JVM arch: "+System.getProperty("sun.arch.data.model"));
		b.append("\n");
		
		b.append("Java home: "+System.getProperty("java.home"));
		b.append("\n");
		b.append("User home: "+getUserHome());
		b.append("\n");
		b.append("User country: "+System.getProperty("user.country"));
		b.append("\n");
		
		b.append("Current dir: "+getCurrentDir());
		b.append("\n");
		
		if (getJavaLibraryPathEnv()!=null) {
			b.append("java.library.path: "+getJavaLibraryPathEnv());
			b.append("\n");
			
		}
		
		
		
		if (System.getProperty("jna.library.path")!=null) {
			b.append("jna.library.path: "+System.getProperty("jna.library.path"));
			b.append("\n");
			
		}
		
		if (getJavaHomeEnv()!=null) {
			b.append("JAVA_HOME: "+getJavaHomeEnv());
			b.append("\n");
		}
		if (getM2HomeEnv()!=null) {
			b.append("M2_HOME: "+getM2HomeEnv());
			b.append("\n");
		}
		if (getM3HomeEnv()!=null) {
			b.append("M3_HOME: "+getM3HomeEnv());
			b.append("\n");
		}
		if (getAntHomeEnv()!=null) {
			b.append("ANT_HOME: "+getAntHomeEnv());
			b.append("\n");
		}
		if (getPathEnv()!=null) {
			b.append("PATH: "+getPathEnv());
			b.append("\n");
		}
		if (getLdLibraryPathEnv()!=null) {
			b.append("LD_LIBRARY_PATH: "+getLdLibraryPathEnv());
			b.append("\n");
		}
		
		b.append("\n");
		
		b.append("All env vars: ");
		b.append(System.getenv());
		b.append("\n");
		return b.toString();
	}
	public static String getJavaLibraryPathEnv() {
		return System.getProperty("java.library.path");
	}
	public static String getPathEnv() {
		return System.getenv("PATH");
	}
	public static String getLdLibraryPathEnv() {
		return System.getenv("LD_LIBRARY_PATH");
	}
	public static String getAntHomeEnv() {
		return System.getenv("ANT_HOME");
	}
	public static String getM3HomeEnv() {
		return System.getenv("M3_HOME");
	}
	public static String getGradleHomeEnv() {
		return System.getenv("GRADLE_HOME");
	}
	public static String getM2HomeEnv() {
		return System.getenv("M2_HOME");
	}
	public static boolean isLinuxGeneric() {
		String ver = getOsVersion();
		String os = getOSName();
		return "Linux".equals(os)&&ver!=null&&ver.contains("generic");
	}
	public static String getOsVersion() {
		return System.getProperty("os.version");
	}
	public static boolean isLinux() {
		String os = getOSName();
		return "Linux".equals(os);
	}
	public static String getPlatformCode() {
		if (isUnix()&&is64Bit()) {
			return "linux-amd64";
		}
		if (isUnix()) {
			return "linux-x86";
		}
		if (isWindows()&&is64Bit()) {
			return "win32-x64";
		}
		if (isWindows()) {
			return "win32-x86";
		}
		throw new IllegalStateException("Invalid platform");
	}
	public static String detectJreLib() {
		String javaHome=getJavaHome();
		if (javaHome==null) {
			LOG.info("Java home was null");
			return null;
		}
		
		try {
			String libFull = FilenameUtils.concat(javaHome, "lib");
			
			if (FileUtils.directoryContains(new File(javaHome), new File(libFull))) {
				String jreLib = FilenameUtils.concat(libFull,
						PlatformHelper.getOSArchitecture());
				LOG.info("Detected JRE lib: {}", jreLib);
				return jreLib;
			}
		} catch (Exception ex) {
			LOG.error("Failed to detect folder 'lib' in "+javaHome,ex);
		}
		try {
			String libJreSub=FilenameUtils.concat(javaHome, "jre/lib");
			if (FileUtils.directoryContains(new File(javaHome), new File(libJreSub))) {
				String jreLib= FilenameUtils.concat(libJreSub,
						PlatformHelper.getOSArchitecture());
				LOG.info("Detected JRE in jre/lib: {}",jreLib);
				return jreLib;
			}
		} catch (Exception ex) {
			LOG.error("Failed to detect folder 'lib' in "+javaHome,ex);
		}
		
		return null;
	}
	
	public static String getJavaHome() {
		if (System.getProperty("java.home")!=null) {
			LOG.info("Java home detected in java.home system property: {}",System.getProperty("java.home"));
			return System.getProperty("java.home");
		} else {
			String javaHomeEnv = getJavaHomeEnv();
			if (javaHomeEnv!=null) {
				LOG.info("Java home detected in JAVA_HOME env variable: {}",javaHomeEnv);
			}
			return javaHomeEnv;
		}
	}
	public static String getJavaHomeEnv() {
		return System.getenv("JAVA_HOME");
	}
	public static String getSqliteFolder() {
		if (isWindows()) {
			return FilenameUtils.concat(getUserHome(),"unibld\\db\\sqlite");
		}
		return "/var/unibld/db/sqlite";
	}
}
