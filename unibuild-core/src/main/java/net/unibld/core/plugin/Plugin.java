package net.unibld.core.plugin;

import net.unibld.core.BuildTask;
import net.unibld.core.config.global.GlobalConfig;

/**
 * Represents a plugin in the UniBuild system.<br>
 * Plugins are customized sets of {@link BuildTask}s and their related task runner spring components
 * that are not included in unibuild-core.<br>
 * Plugin jars must be placed in the plugin directory configured in {@link GlobalConfig} and 
 * are loaded dynamically.<br>
 * Plugin jars must contain all classes that are not included in unibuild-core and its Maven dependencies,
 * in a "shaded"-style.
 * @author andor
 *
 */
public class Plugin {
	private String name;
	private String packagePath;
	private String pluginJarPath;
	
	/**
	 * @return Unique name of the plugin
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name Unique name of the plugin
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Base package of the plugin
	 */
	public String getPackagePath() {
		return packagePath;
	}
	/**
	 * @param packagePath Base package of the plugin
	 */
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}
	/**
	 * @return Path of the plugin JAR
	 */
	public String getPluginJarPath() {
		return pluginJarPath;
	}
	/**
	 * @param pluginJarPath Path of the plugin JAR
	 */
	public void setPluginJarPath(String pluginJarPath) {
		this.pluginJarPath = pluginJarPath;
	}
}
