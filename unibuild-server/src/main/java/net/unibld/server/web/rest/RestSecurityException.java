package net.unibld.server.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception for REST web services signalling a security problem and 
 * sending HTTP status 403 forbidden
 * @author andor
 *
 */
@ResponseStatus(value=HttpStatus.FORBIDDEN,reason="Authentication required")
public class RestSecurityException extends SecurityException {
	private static final long serialVersionUID = -19099108354876842L;
	/**
	 * Default constructor
	 */
	public RestSecurityException() {
		super();
	}
	
	/**
	 * Constructor with message
	 * @param msg Error message
	 */
	public RestSecurityException(String msg) {
		super(msg);
	}
	/**
	 * Constructor with a message and a cause
	 * @param msg Error message
	 * @param cause Cause
	 */
	public RestSecurityException(String msg,Throwable cause) {
		super(msg,cause);
	}
}
