package net.unibld.core.lib;

import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.task.ExecutionResult;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DpkgUtils {
	private static final Logger LOG=LoggerFactory.getLogger(DpkgUtils.class);
	public static boolean isInstalled(CmdExecutor exec,String packageName) {
		
		try {
			CmdContext ctx=new CmdContext();
			ctx.setFailOnError(true);
			ExecutionResult result = exec.execCmd(ctx,String.format("dpkg -l %s"));
			if (result.isSuccess()) {
				if (ctx.getOutput()!=null&&ctx.getOutput().startsWith("No packages found")) {
					LOG.info("No package found: {}",packageName);
					return false;
				}
				if (ctx.getOutput()!=null) {
					String[] lines = ctx.getOutput().split("\n+");
					if (lines!=null&&lines.length>=6) {
						boolean ret = lines[5].startsWith("ii")&&lines[5].contains(packageName);
						if (ret) {
							LOG.info("Package {} is installed",packageName);
						} else {
							LOG.info("Package {} is not installed: output={}",packageName,ctx.getOutput());
						}
						return ret;
					} else {
						LOG.warn("Invalid output: "+ctx.getOutput());
						return false;
					}
				} else {
					LOG.warn("Invalid output: "+ctx.getOutput());
					return false;
				}
			} else {
				LOG.warn("Query installation status failed using dpkg");
				return false;
			}
		} catch (Exception e) {
			LOG.error("Failed to check if package is installed: "+packageName,e);
			return false;
		}
		
	}
}
