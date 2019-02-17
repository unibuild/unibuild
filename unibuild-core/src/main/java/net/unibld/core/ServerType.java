package net.unibld.core;

/**
 * An enum that lists the supported Java application/web server types.
 * 
 * Server names should include a vendor name and a product name but no version
 * unless we want to configure two major versions of the same product in a
 * completely different manner.
 * 
 * This list might be extended in the future.
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:31
 */
public enum ServerType {
	ApacheTomcat,
	Glassfish,
	WebLogic,
	JBoss,
	Websphere
}