package net.unibld.plugins.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.unibld.core.build.BuildException;
import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.exec.CmdContext;
import net.unibld.core.util.PlatformHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command supporting SVN's export command. It is capable of exporting
 * several directories consequently.
 * @author andor
 *
 */
public class SvnExport extends SvnCommand {
	private static final Logger LOG=LoggerFactory.getLogger(SvnExport.class);
	private List<String> multiTargets;
	private String currentTarget;
	private int currentTargetIdx;
	
	private String exportDir;
	private boolean forcing;
	private boolean recursiveExportEnabled=true;

	@Override
	public String getArguments() {
		if (currentTarget!=null) {
			if (!forcing&&currentTargetIdx==0) {
				return String.format("%s %s %s",getCommand(),
						FilenameUtils.concat(getCheckoutDir(),currentTarget),exportDir);
			} else {
				//forcing
				return String.format("%s %s %s --force",getCommand(),
						FilenameUtils.concat(getCheckoutDir(),currentTarget),exportDir);
			}
		} else {
			throw new BuildException("Directory to export was null");
		}
	
	}

	public String getExportDir() {
		return exportDir;
	}

	public void setExportDir(String exportDir) {
		this.exportDir = exportDir;
	}

	@Override
	public void prepare(IBuildContextAttributeContainer attributeContainer,SvnTask task) {
		this.exportDir=task.getExportDir();
		if (this.exportDir!=null) {
			File export=new File(exportDir);
			if (export.getParentFile()!=null) {
				export.getParentFile().mkdirs();
			}
		}
		this.checkoutDir=task.getCheckoutDir();
		this.forcing=task.isForcing();
		multiTargets=getDirsInCheckoutDir();
		if (multiTargets==null||multiTargets.size()==0) {
			throw new BuildException("No directories found in checkout dir: "+this.getCheckoutDir());
		}
	
		currentTargetIdx=0;
		currentTarget=multiTargets.get(currentTargetIdx);
	}

	private List<String> getDirsInCheckoutDir() {
		if (!isAbsolute(checkoutDir)&&!isRelativeSpecified(checkoutDir)) {
			checkoutDir=FilenameUtils.concat(getBuildDir(), checkoutDir);
		}
		File dir=new File(checkoutDir);
		String absolutePath = dir.getAbsolutePath();
		if (!recursiveExportEnabled) {
			
			LOG.info("Recursive export disabled, exporting single directory: {}",absolutePath);
			List<String> lst=new ArrayList<String>();
			lst.add(absolutePath);
			return lst;
		} else {
			
			List<String> dirs=new ArrayList<String>();
			LOG.info("Checking files in dir: {}",dir.getAbsolutePath());
			
			CmdContext ctx=new CmdContext();
			ctx.setFailOnError(true);
			if (PlatformHelper.isLinux()) {
				//need to set rights for the freshly checked out dir
				try {
					executor.execCmd(ctx,"chmod -R 744 "+dir.getAbsolutePath());
					LOG.info("Chmod set to 744 recursively on {}",dir.getAbsolutePath());
				} catch (Exception e) {
					LOG.error("Failed to chmod "+dir.getAbsolutePath(),e);
					throw new BuildException("Failed to chmod "+dir.getAbsolutePath(),e);
				}
				
				//use ls
				try {
					executor.execCmd(ctx,"ls -F --format=single-column "+dir.getAbsolutePath());
					String[] lines = ctx.getOutput().split("\n+");
					for (String line:lines) {
						if (line.trim().endsWith("/")) {
							dirs.add(FilenameUtils.concat(absolutePath, line.trim()));
						}
					}
					
					LOG.info("ls listed {} directories in {}",dirs.size(),dir.getAbsolutePath());
				} catch (Exception e) {
					LOG.error("Failed to ls "+dir.getAbsolutePath(),e);
					throw new BuildException("Failed to ls "+dir.getAbsolutePath(),e);
				}
				
			} else {
			
				Collection<File> list = FileUtils.listFilesAndDirs(dir, TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY);
				
				if (list!=null) {
					for (File f:list) {
						LOG.info("Checking file: {}...",f.getAbsolutePath());
						//File f=new File(absolutePath+"/"+fn);
						if (f.isDirectory()) {
							dirs.add(f.getName());
						}
					}
				} else {
					LOG.warn("File list was null in dir: {}",dir.getAbsolutePath());
				}
			}
			LOG.info("Found {} dirs ({}) in checkout dir: {}",dirs.size(),dirs,dir.getAbsolutePath());
			return dirs;
		}
	}
	
	@Override
	public String getCommand() {
		return "export";
	}
	
	public boolean nextTarget() {
		if (currentTargetIdx>=multiTargets.size()-1) {
			currentTarget=null;
			return false;
		}
		currentTargetIdx++;
		
		currentTarget=multiTargets.get(currentTargetIdx);
		return true;
	}

	public String getCurrentTarget() {
		return currentTarget;
	}

	public boolean isRecursiveExportEnabled() {
		return recursiveExportEnabled;
	}

	public void setRecursiveExportEnabled(boolean recursiveExportEnabled) {
		this.recursiveExportEnabled = recursiveExportEnabled;
	}
}
