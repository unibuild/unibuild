package net.unibld.core.task.impl.util;

import java.util.ArrayList;
import java.util.List;

import net.unibld.core.build.BuildConstants;
import net.unibld.core.task.ExecutionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * A command that cleans (deletes content of) a specific set of folders 
 * @author andor
 *
 */
@Component
public final class CleanTaskRunner extends AbstractCleanTaskRunner<CleanTask> {
	private static final Logger LOG=LoggerFactory.getLogger(CleanTaskRunner.class);
	@Override
	public ExecutionResult execute(CleanTask task) {

		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		List<String> paths=new ArrayList<String>();
		if (task.isComplete()) {
			String buildPath = this.getBuildContextAttribute(task,BuildConstants.VARIABLE_NAME_BUILD_DIR);
			if (buildPath==null) {
				throw new IllegalStateException("Context attribute build.dir not defined");
			}
			logTask("Cleaning build dir {0}...", buildPath);
			paths.add(buildPath);
		} else {
			StringBuilder dirs=new StringBuilder();
			if (task.getDir()!=null&&task.getDir().trim().length()>0) {
				paths.add(task.getDir());
				dirs.append(task.getDir());
				dirs.append(' ');
			}
			if (task.getDirs()!=null&&task.getDirs().trim().length()>0) {
				String[] split = task.getDirs().split(";");
				for (String s:split) {
					paths.add(s);
					dirs.append(s);
					dirs.append(' ');
				}
			}
			logTask("Cleaning dirs: {0}...", dirs.toString());
			
		}
		
		if (paths.size()==0) {
			throw new IllegalArgumentException("No paths have been specified");
		}
		
		try {
			boolean result=true;
			for (String path:paths) {
				result=result&&cleanDir(path);
			}
			
			if (result) {
				return ExecutionResult.buildSuccess();
			} else {
				String msg = "Failed to clean build dirs: "+paths;
				return ExecutionResult.buildError(msg);
			}
		} catch (Exception ex) {
			String msg = "Failed to clean build dirs: "+paths;
			return ExecutionResult.buildError(msg, ex);
		}
		
		
	}


	

	@Override
	protected Logger getLogger() {
		return LOG;
	}


	@Override
	protected String getTaskName() {
		return "clean";
	}
}
