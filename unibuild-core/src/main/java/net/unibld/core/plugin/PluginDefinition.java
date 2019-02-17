package net.unibld.core.plugin;

public class PluginDefinition {
	private String name;
	private String simpleName;
	private String group;
	private String description;
	private String packagePath;
	private boolean internal;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPackagePath() {
		return packagePath;
	}
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}
	
	public String getNameUpper() {
		return toUpper(name);
	}
	private String toUpper(String str) {
		if (str==null) {
			return null;
		}
		if (str.length()==0) {
			return "";
		}
		if (str.length()==1) {
			return str.toUpperCase();
		}
		return str.substring(0,1).toUpperCase()+str.substring(1);
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getNameUpperClean() {
		if (name==null) {
			return null;
		}
		return clean(getNameUpper());
	}
	private String clean(String str) {
		if (str==null) {
			return null;
		}
		return str.replace("-", "").replace("_", "").replace(".","");
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	
	public String getSimpleNameOrName() {
		if (simpleName==null) {
			return name;
		}
		return simpleName;
	}
	
	public String getSimpleNameOrNameUpperClean() {
		if (simpleName==null) {
			if (name==null) {
				return null;
			}
			return clean(toUpper(name));
		}
		return clean(toUpper(simpleName));
	}
	
	public String getSimpleNameOrNameClean() {
		if (simpleName==null) {
			if (name==null) {
				return null;
			}
			return clean(name);
		}
		return clean(simpleName);
	}
	public boolean isInternal() {
		return internal;
	}
	public void setInternal(boolean internal) {
		this.internal = internal;
	}
}
