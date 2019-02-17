package net.unibld.server.service.mail;


import java.util.List;

import net.unibld.server.entities.mail.MailQueueMessage;
import net.unibld.server.entities.mail.MailQueueStatus;

public interface MailQueueService {
	MailQueueMessage addToMailQueue(MailQueueMessage message);
	List<MailQueueMessage> getMessagesWaitingForSend();
	MailQueueMessage saveStatus(MailQueueMessage message, MailQueueStatus status,String errorMessage);
	
	
}
