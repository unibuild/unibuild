package net.unibld.plugins.svn;

import java.io.File;

import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.scm.BaseScmCommand;
import net.unibld.core.scm.IScmCommand;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.PlatformHelper;

import org.slf4j.LoggerFactory;

public abstract class SvnCommand extends BaseScmCommand<SvnTask> implements IScmCommand<SvnTask> {
	protected String checkoutDir;
	protected String repositoryUrl;
	protected boolean useBuiltinStore=true;
	
	@Override
	public String getExecutable() {
		if (PlatformHelper.isWindows()) {
			return "svn.exe";
		} else {
			return "svn";
		}
	}
	public abstract String getCommand();
	
	
	public String getCheckoutDir() {
		return checkoutDir;
	}
	public void setCheckoutDir(String checkoutDir) {
		this.checkoutDir = checkoutDir;
	}
	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}
	

	public void postExecute(ExecutionResult res) {
		
	}
	
	protected boolean isRelativeSpecified(String path) {
		return path.startsWith("..")||path.startsWith(".");
	}
	protected boolean isAbsolute(String path) {
		if (PlatformHelper.isUnix()) {
			return path.startsWith("/");
		} else if (PlatformHelper.isWindows()) {
			return path.charAt(1)==':'&&
					(path.charAt(2)=='\\'||path.charAt(2)=='/');
		}
		return false;
	}
	public void useCheckoutDirAsWorkingDir(IBuildContextAttributeContainer ac, SvnTask task) {
		if (task==null) {
			throw new IllegalArgumentException("Task was null");
		}
		
		if (task.getCheckoutDir()==null) {
			throw new IllegalArgumentException("Checkout dir was null");
		}
		this.checkoutDir=task.getCheckoutDir();
		
		File file = new File(checkoutDir);
		file.mkdirs();
		
		LoggerFactory.getLogger(getClass()).info("Using checkout dir: {}",checkoutDir);
		this.workingDirectory=file;
		
	}

	
	
}
