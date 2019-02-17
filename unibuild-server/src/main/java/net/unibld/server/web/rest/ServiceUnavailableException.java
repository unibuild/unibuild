package net.unibld.server.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception for REST web services signalling that the service is unavailable
 * because of maintenance (HTTP status 503)
 * @author andor
 * 
 *
 */
@ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE,reason="The server is undergoing maintenance")
public class ServiceUnavailableException extends SecurityException {
	private static final long serialVersionUID = 2656227807698910199L;
	public ServiceUnavailableException() {
		super();
	}
	
	public ServiceUnavailableException(String msg) {
		super(msg);
	}
	public ServiceUnavailableException(String msg,Throwable cause) {
		super(msg,cause);
	}
}
