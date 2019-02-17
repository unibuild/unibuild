package net.unibld.core.task.impl.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import net.unibld.core.BuildTask;
import net.unibld.core.build.BuildException;
import net.unibld.core.task.BaseTaskRunner;

/**
 * Base class for Java based tasks that clean one or more directories.
 * @author andor
 *
 */
public abstract class AbstractCleanTaskRunner<T extends BuildTask> extends BaseTaskRunner<T>{
	protected boolean cleanDir(String dirPath) {
		File dir=new File(dirPath);
		
		boolean ret=true;
		if (dir.exists()&&dir.isDirectory()) {
			logInfo(String.format("Deleting build dir %s and its contents...",dir.getAbsolutePath()));
			boolean result=false;
			try {
				FileUtils.deleteDirectory(dir);
				result=true;
			} catch (Exception ex) {
				LoggerFactory.getLogger(getClass()).error("Failed to delete directory: "+dir.getAbsolutePath(),ex);
				throw new BuildException("Failed to delete directory: "+dir.getAbsolutePath(),ex);
			}
			ret=ret&&result;
			LoggerFactory.getLogger(getClass()).debug("Deleted build dir {} and its contents",dir.getAbsolutePath());
		}
		
		if (ret) {
			LoggerFactory.getLogger(getClass()).info("Successfully cleaned dir: {}",dir.getAbsolutePath());
		} else {
			LoggerFactory.getLogger(getClass()).warn("Failed to clean dir: {}",dir.getAbsolutePath());
		}
		return ret;
	}
}
