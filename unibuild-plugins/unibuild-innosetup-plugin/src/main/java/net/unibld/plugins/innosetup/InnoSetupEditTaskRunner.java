package net.unibld.plugins.innosetup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.version.VersionContext;
import net.unibld.core.util.version.VersionHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * A runner for {@link InnoSetupEditTask} that edits an Innosetup configuration file
 * @author andor
 *
 */
@Component
public class InnoSetupEditTaskRunner extends BaseTaskRunner<InnoSetupEditTask> {
	private static final Logger LOG=LoggerFactory.getLogger(InnoSetupEditTaskRunner.class);
	
	
	static class Define {
		private String name;
		private String value;
		public Define(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Define other = (Define) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
	}
	
	
	private Map<String,Define> definesToSet;

	protected ExecutionResult execute(InnoSetupEditTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		logTask("Editing InnoSetup .iss file: {0}...", task.getIssPath());
			
		
		if (task.getIssPath()==null) {
			throw new IllegalArgumentException(".iss file not specified");
		}
		
		
		File iss=new File(task.getIssPath());
		if (!iss.exists()||!iss.isFile()) {
			throw new IllegalArgumentException("Invalid .iss file: "+task.getIssPath());
		}
		
		List<String> lines =null;
		try {
			lines = FileUtils.readLines(iss,task.getEncoding()!=null?task.getEncoding():"UTF-8");
			
			
		} catch (IOException e) {
			logError("Could not read .iss file: "+task.getIssPath(),e);
			return ExecutionResult.buildError("Could not read .iis file"+iss.getAbsolutePath(), e);
		}
		
		if (lines==null||lines.size()==0) {
			return ExecutionResult.buildError("Empty .iis file"+iss.getAbsolutePath());
		}
		
		definesToSet=new HashMap<String,InnoSetupEditTaskRunner.Define>();
		if (task.getDefines()!=null&&task.getDefines().trim().length()>0) {
			String[] defDefinitions=task.getDefines().split(",");
			for (String def:defDefinitions) {
				String[] kv=def.trim().split("=");
				if (kv.length!=2) {
					throw new IllegalArgumentException("Invalid defines value: "+task.getDefines());
				}
				definesToSet.put(kv[0],new Define(kv[0], kv[1]));
			}
		}
		
		List<String> editedLines=editLines(task,lines);
		
		try {
			FileUtils.writeLines(iss, task.getEncoding()!=null?task.getEncoding():"UTF-8",editedLines);
			logInfo("Successfully edited .iss file: "+iss.getAbsolutePath());
			return ExecutionResult.buildSuccess();
			
		} catch (IOException e) {
			logError("Could not write .iss file: "+task.getIssPath(),e);
			return ExecutionResult.buildError("Could not write .iis file"+iss.getAbsolutePath(), e);
		}
		
		
	}

	private List<String> editLines(InnoSetupEditTask task,List<String> lines) {
		List<String> ret=new ArrayList<String>();
		for (String line:lines) {
			ret.add(editLine(task,line));
		}
		return ret;
	}



	private String editLine(InnoSetupEditTask task,String line) {
		if (line==null) {
			throw new IllegalArgumentException("Line was null");
		}
		
		if (line.trim().length()==0) {
			return line;
		}
		
		if (line.startsWith("#define")) {
			return editDefine(task,line);
		}
		if (line.startsWith("AppVersion=")) {
			return editAppVersion(task,line);
		}
		return line;
	}



	private String editDefine(InnoSetupEditTask task,String line) {
		String[] split = line.split(" ");
		if (split.length>=2) {
			String defineName=split[1].trim();
			if (definesToSet.containsKey(defineName)) {
				Define d=definesToSet.get(defineName);
				logInfo(String.format("Setting #define value for %s: %s",defineName,d.getValue()));
				return String.format("#define %s \"%s\"", defineName,d.getValue());
			}
			
			boolean pom = !StringUtils.isEmpty(task.getPomFile());
			boolean buildNum = !StringUtils.isEmpty(task.getBuildNumberProperties());
			if (pom||buildNum) {
				if (!StringUtils.isEmpty(task.getVersionDefineName())&&defineName.equals(task.getVersionDefineName())) {
					VersionContext ctx=new VersionContext();
					ctx.setPom(pom);
					ctx.setBuildNum(buildNum);
					ctx.setPomFile(task.getPomFile());
					ctx.setBuildNumberProperties(task.getBuildNumberProperties());
					ctx.setBuildNumberPropertyName(task.getBuildNumberPropertyName());
					ctx.setProjectConfig(getProjectConfig(task));
					ctx.setTaskLogger(this);
					return String.format("#define %s \"%s\"", defineName,VersionHelper.getVersionFromPomOrProperties(ctx));
				}
			}
		}
		return line;
	}



	private String editAppVersion(InnoSetupEditTask task,String line) {
		if (task.getVersion()!=null&&task.getVersion().trim().length()>0) {
			return "AppVersion="+task.getVersion().trim();
		}
		
		boolean pom = !StringUtils.isEmpty(task.getPomFile());
		boolean buildNum = !StringUtils.isEmpty(task.getBuildNumberProperties());
		if (pom||buildNum) {
			if (StringUtils.isEmpty(task.getVersionDefineName())) {
				VersionContext ctx=new VersionContext();
				ctx.setPom(pom);
				ctx.setBuildNum(buildNum);
				ctx.setPomFile(task.getPomFile());
				ctx.setBuildNumberProperties(task.getBuildNumberProperties());
				ctx.setBuildNumberPropertyName(task.getBuildNumberPropertyName());
				ctx.setProjectConfig(getProjectConfig(task));
				ctx.setTaskLogger(this);
				return "AppVersion="+VersionHelper.getVersionFromPomOrProperties(ctx);
			}
		}
		return line;
	}





	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "innosetup-edit";
	}
}//end CustomNativeTaskRunner