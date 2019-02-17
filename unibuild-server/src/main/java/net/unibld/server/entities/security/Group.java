package net.unibld.server.entities.security;

/**
 * Possible user group types
 * @author andor
 *
 */
public enum Group {
	/**
	 * Administrator: all rights
	 */
	ROLE_ADMIN,
	/**
	 * Super-user: can create/edit/print all tasks in own language and read/print all other tasks in all languages
	 */
	ROLE_SUPER_USER,
	
	/**
	 * A role that can only view the website
	 */
	ROLE_USER;

	
}
