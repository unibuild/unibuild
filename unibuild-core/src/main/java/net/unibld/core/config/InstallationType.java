package net.unibld.core.config;

/**
 * Installation type enum
 * @author andor
 *
 */
public enum InstallationType {
	/**
	 * Installed in a specific folder that will be specified by the user
	 */
	folder,
	/**
	 * Installed on the PATH environment variable so the executable will be
	 * called without any path
	 */
	path
}
