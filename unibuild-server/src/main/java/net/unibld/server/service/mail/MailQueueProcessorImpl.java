package net.unibld.server.service.mail;


import java.util.ArrayList;
import java.util.List;

import net.unibld.core.mail.EmailService;
import net.unibld.core.mail.MailAttachment;
import net.unibld.core.mail.MailSendResult;
import net.unibld.server.entities.mail.MailQueueAttachment;
import net.unibld.server.entities.mail.MailQueueMessage;
import net.unibld.server.entities.mail.MailQueueStatus;
import net.unibld.server.repositories.mail.MailQueueAttachmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("mailQueueProcessor")
public class MailQueueProcessorImpl implements MailQueueProcessor{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailQueueProcessorImpl.class);

	public static final String USER_EMAIL_JOB = "EMAIL_JOB";
	
	@Autowired
	private MailQueueService queueService;
	
	@Autowired
	private MailQueueAttachmentRepository attachmentRepo;
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private ApplicationContext appContext;
	
	
	public MailQueueProcessResult processMailsWaitingForSend() {
		List<MailQueueMessage> messages = queueService.getMessagesWaitingForSend();
		LOGGER.info("Processing mails waiting for send: {}...",messages.size());
		
		MailQueueProcessResult res=new MailQueueProcessResult();
		res.setTotal(messages.size());
		
		for (MailQueueMessage message:messages) {
			MailCallbackWrapper callback=getCallback(message);
			
			try {
				String[] to = splitAddress(message.getTo());
				String[] cc = splitAddress(message.getCc());
				String[] bcc = splitAddress(message.getBcc());
				MailSendResult result=emailService.sendMail(to,cc,bcc, message.getSubject(), message.getBody(), getAttachments(message));
				if (result.isSuccess()) {
					LOGGER.info("Successfully sent mail to: "+createMailLogMessage(message));
					queueService.saveStatus(message,MailQueueStatus.SENT,null);
					
					if (callback!=null) {
						callback.sendCompleted();
					}
					res.addSuccess();
				} else {
					LOGGER.error("Failed to send mail to: "+createMailLogMessage(message));
					queueService.saveStatus(message,MailQueueStatus.FAILED,result.getErrorMessage());
					if (callback!=null) {
						callback.sendFailed();
					}
					res.addError();
				}
			} catch (Exception ex) {
				LOGGER.error("Failed to send mail to: "+createMailLogMessage(message),ex);
				queueService.saveStatus(message,MailQueueStatus.FAILED,ex.getMessage());
				if (callback!=null) {
					callback.sendFailed();
				}
				res.addError();
			}
		}
		
		
		LOGGER.info("Processing mail queue completed: success={}, failed={}, total={}",
				res.getSucceeded(),res.getFailed(),res.getTotal());
		return res;
	}


	private String[] splitAddress(String address) {
		if (StringUtils.isEmpty(address)) {
			return new String[0];
		}
		return address.split(",");
	}


	private MailCallbackWrapper getCallback(MailQueueMessage message) {
		if (message==null) {
			throw new IllegalArgumentException("MailQueueMessage was null");
		}
		if (message.getCallbackClass()==null) {
			return null;
		}
		
		try {
			Class<? extends MailCallback> klazz=(Class<? extends MailCallback>)Class.forName(message.getCallbackClass());
			MailCallback bean = appContext.getBean(klazz);
			MailCallbackWrapper ret=new MailCallbackWrapper();
			ret.setCallback(bean);
			ret.setParam(message.getCallbackParameter());
			return ret;
		} catch (Exception ex) {
			throw new IllegalStateException("Could not invoke callback class",ex);
		}
	}


	private String createMailLogMessage(MailQueueMessage message) {
		return String.format("To:%s Cc:%s Bcc:%s Subject: %s",message.getTo()!=null?message.getTo():"-",
				message.getCc()!=null?message.getCc():"-",
				message.getBcc()!=null?message.getBcc():"-",
				message.getSubject());
	}

	private List<MailAttachment> getAttachments(MailQueueMessage message) throws Exception {
		List<MailQueueAttachment> result = attachmentRepo.findByMessage(message);
		List<MailAttachment> ret=new ArrayList<MailAttachment>();
		ret.addAll(result);
		return ret;

	}


	
}
