package net.unibld.core.security;

/**
 * An enum representing different applicable strategies when confronted with a need for a password
 * to a specific resource during a build run.
 * @author andor
 *
 */
public enum PasswordStrategy {
	/**
	 * This strategy interactively asks the starter of the build for the password
	 */
	ask,
	/**
	 * This strategy tries to look up the password from a dedicated store.
	 */
	stored
}
