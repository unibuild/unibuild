package net.unibld.core.remote;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.unibld.core.util.Zip;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteBuildClient {
	private static final Logger LOGGER=LoggerFactory.getLogger(RemoteBuildClient.class);
	private String host;
	private String projectPath;
	private String buildDir;
	private String[] includes;
	private String[] excludes;
	private RemoteBuildServiceClient service;
	private boolean skipCleanup;
	private File zipFile;
	private String outputFileName;
	private String outputDir;
	private String errorMessage;
	private String ticket;
	private String goal;
	
	public RemoteBuildClient(String host, String ticket,String projectPath) {
		this.host = host.replace("\"", "");
		this.projectPath=projectPath;
		this.ticket=ticket;
	}

	public boolean executeBuild() {
		try {
			LOGGER.info(
					"Starting remote build...");
	
			zipFile = compressBuildDir();
			LOGGER.info(
					"Build dir compressed to: " + zipFile.getAbsolutePath());
	
			this.service=new RemoteBuildServiceClient(host);
			
			
			
			String id = uploadAndStartBuild(zipFile);
	
			while (isBuildRunning(id)) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOGGER.error("Sleep failed",e);
				}
			}
	
			if (isBuildCompleted(id)) {
				LOGGER.info("Remote build succeeded, downloading output files...");
				boolean success = downloadOutputFiles(id);
				if (success) {
					buildSuccess();
					return true;
				} else {
					buildError(id);
					return false;
				}
			} else {
				buildError(id);
				return false;
			}
		} catch (Exception ex) {
			LOGGER.error("Failed to execute build",ex);
			return false;
		} finally {
			cleanup();
		}
	}

	private void cleanup() {
		if (!skipCleanup) {
			if (zipFile!=null&&zipFile.getParentFile()!=null&&zipFile.getParentFile().exists()) {
				try {
					FileUtils.deleteDirectory(zipFile.getParentFile());
					LOGGER.info("Cleaned up folder: "+zipFile.getParentFile().getAbsolutePath());
					
				} catch (IOException e) {
					LOGGER.error("Failed to cleanup folder: "+zipFile.getParentFile().getAbsolutePath(),e);
				}
				
			}
		}
	}

	private void buildError(String id) {
		try {
			String error=service.getBuildError(ticket,id);
			if (error!=null) {
				LOGGER.error("Remote build failed with error: "+error);
				this.errorMessage=error;
			} else {
				LOGGER.error("Remote build failed with an unknown error");
			}
			
		} catch (Exception ex) {
			LOGGER.error("Failed to get error message after remote build failure",ex);
		}
		
	}

	
	private void buildSuccess() {
		LOGGER.info("Build succeeded.");
	}

	private boolean downloadOutputFiles(String id) throws IOException {
		String filePath=service.downloadOutput(ticket,id,outputFileName,outputDir);
		if (filePath==null) {
			LOGGER.error("File downloaded was null");
			return false;
		}
		File f=new File(filePath);
		if (!f.exists()||!f.isFile()) {
			LOGGER.error("File downloaded was invalid: "+filePath);
			return false;
		}
		
		//Zip.unzip(f.getAbsolutePath(), outputDir);
		return true;
	}

	private boolean isBuildCompleted(String id) throws Exception {
		return service.isBuildCompleted(ticket, id);
	}

	private boolean isBuildRunning(String id) throws Exception {
		return service.isBuildRunning(ticket, id);
	}

	private String uploadAndStartBuild(File file) throws Exception {
		BuildStartedDto res= this.service.uploadZip(ticket, projectPath, goal, file);
		
		if (res==null) {
			throw new IllegalStateException("Result DTO was null");
		}
		if (res.getId()==null) {
			throw new IllegalStateException("Generated build ID was null");
		}
		if (res.isStarted()&&res.getStartTime()!=null) {
			LOGGER.info(String.format("Uploaded file %s to host %s and received generated build id: %s at %s",file.getAbsolutePath(),host,res.getId(),res.getStartTime()));
		
		} else {
			if (res.getErrorMessage()!=null) {
				String msg = String.format("Failed to start remote build at host %s and with error message: %s",host,res.getErrorMessage());
				LOGGER.error(msg);
				
				throw new RemoteBuildException(msg);
			} else {
				String msg = String.format("Failed to start remote build at host %s and with no error message",host);
				LOGGER.error(msg);
				throw new RemoteBuildException(msg);
				
			}
		}
		return res.getId();
	}

	private File compressBuildDir() throws IOException {
		String tempFilePath = getTempFilePath();
		
		if (includes==null&&excludes==null) {
			LOGGER.info(String.format("Compressing build dir: %s...",buildDir));
			Zip.zip(buildDir, tempFilePath);	
		} else {
			LOGGER.info(String.format("Compressing build dir: %s, includes=%s, excludes=%s...",buildDir,traceArray(includes),traceArray(excludes)));
			
			File build=new File(buildDir);
			String buildDirName=build.getName();
			
			File tempFile=new File(tempFilePath);
			File tempDir=tempFile.getParentFile();
			String tmpPath=FilenameUtils.concat(tempDir.getAbsolutePath(), buildDirName);
			File tmpBuildDir = new File(tmpPath);
			tmpBuildDir.mkdirs();
			
			FileUtils.copyDirectory(build, tmpBuildDir, new FileFilter() {
				
				public boolean accept(File pathname) {
					if (includes!=null&&!arrayContainsFile(includes,pathname)&&!arrayStartsWithFile(includes,pathname)) {
						return false;
					}
					
					if (excludes!=null&&arrayContainsFile(excludes,pathname)) {
						return false;
					}
					return true;
				}
			});
			
			Zip.zip(tmpPath, tempFilePath);
		}
		
		return new File(tempFilePath);
	}

	protected boolean arrayContainsFile(String[] arr,File file) {
		if (arr==null) {
			throw new IllegalArgumentException("Array was null");
		}
		
		for (String str:arr) {
			String path=FilenameUtils.concat(buildDir, str);
			String absPath=new File(path).getAbsolutePath();
			if (absPath.equals(file.getAbsolutePath())) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean arrayStartsWithFile(String[] arr,File file) {
		if (arr==null) {
			throw new IllegalArgumentException("Array was null");
		}
		
		for (String str:arr) {
			String path=FilenameUtils.concat(buildDir, str);
			String absPath=new File(path).getAbsolutePath();
			if (file.getAbsolutePath().startsWith(absPath)) {
				return true;
			}
		}
		return false;
	}

	private String traceArray(String[] arr) {
		if (arr == null || arr.length == 0) {
			return "-";
		}

		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				b.append(',');
			}
			b.append(arr[i]);

		}
		b.append(']');
		return b.toString();
	}

	private static SimpleDateFormat DF = new SimpleDateFormat("yyyyMMdd_HHmmss");

	private String getTempFilePath() {
		String home = System.getProperty("user.home");
		if (home == null) {
			home = ".";
		}
		String baseDir = FilenameUtils.concat(home, "RemoteBuildClient");
		String ret = FilenameUtils.concat(baseDir, DF.format(new Date()));
		File dir = new File(ret);
		dir.mkdirs();
		return FilenameUtils.concat(dir.getAbsolutePath(), "upload.zip");
	}

	
	public void initialize(Map<String, OptArg> optArgs) {
		if (optArgs == null) {
			throw new IllegalArgumentException("Options map was null");
		}
		
		
		
		if (optArgs.containsKey("build-dir")) {
        	buildDir=optArgs.get("build-dir").value;
        } else {
        	buildDir=".";
        	
        }
		
	
		if (optArgs.containsKey("output-dir")) {
        	outputDir=optArgs.get("output-dir").value;
        } else {
        	outputDir=null;
        	
        }
		
		
		File dir = new File(buildDir);
		if (!dir.exists() || !dir.isDirectory()) {
			LOGGER.error(
					"Build dir does not exist: " + buildDir);
			throw new IllegalStateException("Build dir does not exist: "
					+ buildDir);
		}

		buildDir = dir.getAbsolutePath();
		
		File odir = new File(outputDir);
		if (!odir.exists() || !odir.isDirectory()) {
			boolean succ=odir.mkdirs();
			if (!succ) {
				throw new RemoteBuildException("Failed to create local output dir: "+outputDir);
			}
		}

		outputDir = odir.getAbsolutePath();
		
		

	
		if (optArgs.containsKey("includes")) {
			includes = optArgs.get("includes").value.split(",");
		} 

		if (optArgs.containsKey("excludes")) {
			excludes = optArgs.get("excludes").value.split(",");
		}
		
		if (optArgs.containsKey("skip-cleanup")) {
			skipCleanup="true".equals(optArgs.get("skip-cleanup").value);
		}

		LOGGER
				.info(String.format(
								"Remote build client inited with: host=%s, project=%s, buildDir=%s, goal=%s, outputDir=%s",
								host, projectPath, buildDir,goal,outputDir));
	}

	public String[] getIncludes() {
		return includes;
	}

	public void setIncludesString(String str) {
		
		if (str!=null) {
			includes = str.split(",");
		} else {
			includes = null;
		
		}
	}
	public void setExcludesString(String str) {
		if (str!=null) {
			excludes = str.split(",");
		} 
	}
	public void setIncludes(String[] includes) {
		this.includes = includes;
	}

	public String[] getExcludes() {
		return excludes;
	}

	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	public boolean isSkipCleanup() {
		return skipCleanup;
	}

	public void setSkipCleanup(boolean skipCleanup) {
		this.skipCleanup = skipCleanup;
	}

	

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setHost(String host) {
		this.host = host;
	}

	
	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}

	public String getBuildDir() {
		return buildDir;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

}
