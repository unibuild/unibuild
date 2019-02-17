package net.unibld.server.service.mail;

public class MailCallbackWrapper {
	private MailCallback callback;
	private String param;
	public MailCallback getCallback() {
		return callback;
	}
	public void setCallback(MailCallback callback) {
		this.callback = callback;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public void sendCompleted() {
		callback.sendCompleted(param);
	}
	public void sendFailed() {
		callback.sendFailed(param);
	}
}
