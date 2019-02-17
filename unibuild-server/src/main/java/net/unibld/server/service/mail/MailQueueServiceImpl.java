package net.unibld.server.service.mail;


import java.util.Date;
import java.util.List;

import net.unibld.server.entities.mail.MailQueueMessage;
import net.unibld.server.entities.mail.MailQueueStatus;
import net.unibld.server.repositories.mail.MailQueueRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MailQueueServiceImpl implements MailQueueService {
	private static final Logger LOGGER=LoggerFactory.getLogger(MailQueueServiceImpl.class);
	
	@Value("${smtp.queue.maxcount:20}")
	private int maxCount;
	
	@Autowired
	private MailQueueRepository mailQueueRepo;
	
	
	
	@Override
	public MailQueueMessage addToMailQueue(MailQueueMessage message) {
		if (message==null) {
			throw new IllegalArgumentException("Message was null");
		}
		if (message.getTo()==null&&message.getCc()==null&&message.getBcc()==null) {
			throw new IllegalArgumentException("To, cc and bcc address cannot be null at the same time");
		}
		if (message.getSubject()==null) {
			LOGGER.error("Message subject was null: "+message.getTo());
			
			throw new IllegalArgumentException("Subject cannot be null");
		}
		if (message.getBody()==null) {
			LOGGER.error("Message body was null: "+message.getTo());
			
			throw new IllegalArgumentException("Body cannot be null");
		}
		
		return mailQueueRepo.save(message);
	}

	public List<MailQueueMessage> getMessagesWaitingForSend() {
		Pageable top = new PageRequest(0, maxCount);
		return mailQueueRepo.findByStatusOrderByCreateDate(MailQueueStatus.WAITING_FOR_SEND, top);
	}

	@Override
	public MailQueueMessage saveStatus(MailQueueMessage message, MailQueueStatus status,String errorMessage) {
		if (message==null) {
			throw new IllegalArgumentException("Message was null");
		}
		if (message.getId()==null) {
			throw new IllegalArgumentException("Message id was null");
		}
		message.setFailureReason(errorMessage);
		message.setStatus(status);
		if (status==MailQueueStatus.SENT) {
			message.setSendDate(new Date());
		}
		return mailQueueRepo.save(message);
	}

	
}
