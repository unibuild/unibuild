package net.unibld.server.service.security;


/**
 * A business exception thrown by Spring Security
 * @author andor
 *
 */
public class SpringSecurityException extends Exception {

	private static final long serialVersionUID = 8525995107667677230L;

	/**
	 * @param msg Error message
	 */
	public SpringSecurityException(String msg) {
		super(msg);
	}
	/**
	 * @param msg Error message
	 * @param t Root cause
	 */
	public SpringSecurityException(String msg,Throwable t) {
		super(msg,t);
	}
}
