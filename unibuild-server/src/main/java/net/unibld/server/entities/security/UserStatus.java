package net.unibld.server.entities.security;


/**
 * An enum defining user statuses
 * @author andor
 *
 */
public enum UserStatus {
	/**
	 * Valid and active registered user
	 */
	REGISTERED,
	/**
	 * Banned user
	 */
	BANNED,
	/**
	 * Deleted user
	 */
	DELETED,
	/**
	 * Inactive user (registered but not activated)
	 */
	INACTIVE;
	
}
