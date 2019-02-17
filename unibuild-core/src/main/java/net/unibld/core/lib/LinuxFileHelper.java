package net.unibld.core.lib;

import java.io.File;

import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.task.ExecutionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinuxFileHelper {
	private static final Logger LOG = LoggerFactory.getLogger(LinuxFileHelper.class);
	
	public static FileState getFileState(CmdExecutor exec,String path) {
		if (path==null) {
			throw new IllegalArgumentException("Path was null");
		}
		try {
			CmdContext ctx=new CmdContext();
			ctx.setFailOnError(true);
			ExecutionResult res = exec.execCmd(ctx,String.format("stat -c \"%%F %%n\" %s",new File(path).getAbsolutePath()));
			String output=null;
			if (ctx.getOutput()!=null) {
				LOG.info("Stat output: {}",ctx.getOutput().trim());
				output=ctx.getOutput().trim().replace("\"", "");
			}
			if (res.isSuccess()&&output!=null&&output.startsWith("regular file")) {
				return FileState.FILE;
			}
			if (res.isSuccess()&&output!=null&&output.startsWith("directory")) {
				return FileState.DIR;
			}
			
			return FileState.NOT_EXISTS;
		} catch (Exception ex) {
			LOG.error("Failed to check file state",ex);
			return FileState.NOT_EXISTS;
		}
		
	}
	
	
	/*public static List<ProcessInfo> ps() {
		try {
			ExecutionResult res = exec.execCmd("ps -aux",null, true, null);
			String output=null;
			if (exec.getOutput()!=null) {
				LOG.info("ps output: {}",exec.getOutput().trim());
				output=exec.getOutput().trim().replace("\"", "");
			}
			if (res.isSuccess()&&output!=null&&output.startsWith("regular file")) {
				return FileState.FILE;
			}
			if (res.isSuccess()&&output!=null&&output.startsWith("directory")) {
				return FileState.DIR;
			}
			
			return FileState.NOT_EXISTS;
		} catch (Exception ex) {
			LOG.error("Failed to ps",ex);
			return FileState.NOT_EXISTS;
		}
	}*/
}
