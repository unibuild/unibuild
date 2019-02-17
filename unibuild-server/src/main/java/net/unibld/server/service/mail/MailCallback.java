package net.unibld.server.service.mail;

public interface MailCallback {
	public void sendCompleted(String param);
	public void sendFailed(String param);
}
