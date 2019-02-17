package net.unibld.server.service.mail;

public interface MailQueueProcessor {
	public MailQueueProcessResult processMailsWaitingForSend();
}
