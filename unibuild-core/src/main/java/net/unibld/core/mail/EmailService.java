package net.unibld.core.mail;

import java.util.List;

/**
 * Interface for sending emails.
 * @author andor
 *
 */
public interface EmailService {
	MailSendResult sendMail(String[] to, String[] cc, String[] bcc, String from,
							String subject, String body, List<MailAttachment> attachments);
	MailSendResult sendMail(List<String> recipients,String subject,String body,List<MailAttachment> attachments);
	MailSendResult sendMail(String[] recipients,String subject,String body,List<MailAttachment> attachments);

	MailSendResult sendMail(String recipient, String subject, String body);
	MailSendResult sendMail(String[] to, String[] cc, String[] bcc,
			String subject, String body, List<MailAttachment> attachments);

	String getHost();

	int getPort();

	String getFrom();

	boolean isMailSendEnabled();
}
