package net.unibld.core.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple static context for platform independent unit tests
 * @author andor
 *
 */
public class UnitTestContext {
	private static final Logger LOG=LoggerFactory.getLogger(UnitTestContext.class);
	
	/**
	 * Returns the base directory of tests. This is either {user.home}/unibld/test
	 * or {tmp.dir}/unibld/test, where {user.home} is the user's home directory if
	 * it exists, and {tmp.dir} is a tmp directory in the root folder of the drive
	 * @return Base directory for unit tests
	 */
	public static String getBaseDir() {
		return "./target/test";
	}
	
	

	/**
	 * Tries to detect project directory from the user's current directory
	 * @return The project directory if it could be found, null otherwise
	 */
	public static String getProjectDir() {
		
		String projDir= detectProjectDir(".");
		if (projDir==null) {
			projDir=detectProjectDir("..");
		}
		
		if (projDir!=null) {
			String ret = new File(projDir).getAbsolutePath();
			LOG.info("Project dir detected: {}",ret);
			return ret;
		} else {
			throw new IllegalStateException("Project dir could not be detected");
		}
	
	}

	private static String detectProjectDir(String path) {
		File dir=new File(path);
		if (!dir.exists()||!dir.isDirectory()) {
			LOG.info("Path is not a directory: {}",path);
			return null;
		}
		String[] fileList = dir.list();
		for (String f:fileList) {
			if ("src".equals(f)) {
				String p=FilenameUtils.concat(dir.getAbsolutePath(), f);
				File file=new File(p);
				if (file.exists()&&file.isDirectory()) {
					LOG.info("Directory src found in: {}",dir.getAbsolutePath());
					return dir.getAbsolutePath();
				}
			}
		}
		
		LOG.info("Directory src could not be found in: {}",dir.getAbsolutePath());
		return null;
	}

	private static final DateFormat DF=new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
	
	public static String extractResource(Class<?> testClass,
			String resourcePath) throws URISyntaxException, IOException {
		URL url = testClass.getResource(resourcePath);
		if (url==null) {
			throw new IllegalStateException("Resource not found: "+resourcePath);
		}
		File resourceFile = new File(url.toURI());
		String testDir=FilenameUtils.concat(UnitTestContext.getBaseDir(),testClass.getSimpleName());
		String extractDir=FilenameUtils.concat(testDir,DF.format(new Date()));
		File targetDir=new File(extractDir);
		targetDir.mkdirs();
		
		FileUtils.copyFileToDirectory(resourceFile, targetDir);
		return FilenameUtils.concat(targetDir.getAbsolutePath(), resourceFile.getName());
		
	}
	public static String extractResourceTo(Class<?> testClass,
			String resourcePath,String extractDir) throws URISyntaxException, IOException {
		URL url = testClass.getResource(resourcePath);
		File resourceFile = new File(url.toURI());
		File targetDir=new File(extractDir);
		targetDir.mkdirs();
		
		FileUtils.copyFileToDirectory(resourceFile, targetDir);
		return FilenameUtils.concat(targetDir.getAbsolutePath(), resourceFile.getName());
		
	}


	/**
	 * Stores the original user directory when invoked for the first time and checks
	 * for the existence of native dependencies on the library path.<br>
	 * Storing the original directory path is necessary because tasks like {@link CdTask}
	 * can change the user directory while running the unit tests
	 */
	public static void initialize() {
		File pluginsDir=new File("./target/plugins");
		pluginsDir.mkdirs();
		
	}
	
	public static String getFolderPath(String folder,String taskName) {
		return FilenameUtils.concat(getTaskFolder(taskName),folder);
	}



	public static String getTaskFolder(String taskName) {
		return FilenameUtils.concat(UnitTestContext.getBaseDir(), taskName);
	}
	public static File getFolder(String folder,String taskName) {
		return new File(getFolderPath(folder,taskName));
	}
}
