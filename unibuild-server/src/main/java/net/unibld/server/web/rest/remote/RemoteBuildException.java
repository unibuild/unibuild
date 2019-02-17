package net.unibld.server.web.rest.remote;

public class RemoteBuildException extends RuntimeException {

	public RemoteBuildException(String msg, Exception ex) {
		super(msg,ex);
	}

	public RemoteBuildException(String msg) {
		super(msg);
	}

}
