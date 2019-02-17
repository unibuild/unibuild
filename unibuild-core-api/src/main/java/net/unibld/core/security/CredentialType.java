package net.unibld.core.security;

/**
 * Enum for possible credential types
 * @author andor
 *
 */
public enum CredentialType {
	/**
	 * No credentials provided
	 */
	NONE,
	/**
	 * Provided by an external software
	 */
	EXTERNALLY_PROVIDED,
	/**
	 * Provided during build run
	 */
	BUILD_PROVIDED
}
