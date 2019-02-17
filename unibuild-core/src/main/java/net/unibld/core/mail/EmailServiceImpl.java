package net.unibld.core.mail;

import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Spring implementation of the {@link EmailService} interface, using Spring Integration's
 * {@link JavaMailSenderImpl}.
 * @author andor
 *
 */
@Service
public class EmailServiceImpl implements EmailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

	
	@Value("${mail.smtp.host:}")
	private String host;
	@Value("${mail.smtp.port:25}")
	private int port;

	@Value("${mail.smtp.user:}")
	private String userName;
	@Value("${mail.smtp.password:}")
	private String password;
	
	@Value("${mail.smtp.from:}")
	private String from;
	
	@Value("${mail.send.enabled:true}")
	private boolean mailSendEnabled;
	
	@Value("${mail.smtp.auth:true}")
	private boolean usesAuthentication;
	@Value("${mail.smtp.starttls.enable:true}")
	private boolean startTlsEnable;
	
	@Value("${mail.smtp.ssl.enable:true}")
	private boolean sslEnabled;
	
	

	@Autowired
	private JavaMailSenderImpl sender;

	
	@PostConstruct
	public void init() {
		// as of http://stackoverflow.com/questions/30628139/set-mail-strictly-mime-parm-folding-in-javamail
		System.setProperty("mail.mime.encodeparameters", "false"); 
		System.setProperty("mail.mime.encodefilename", "true");
	
	}
	
	protected MailSendResult sendMail(String[] to,MimeMessagePreparator preparator) {
		if (StringUtils.isEmpty(host)) {
			LOGGER.warn("SMTP host is not configured, skipping...");
			MailSendResult result=new MailSendResult();
			result.setErrorMessage("SMTP host is not configured");
			result.setSuccess(false);
			return result;
		}
		MailSendResult result=new MailSendResult();
		result.setTo(to);
		try {
			if (mailSendEnabled) {
				sender.send(preparator);
				LOGGER.info(String.format("Mail [%s] successfully sent",preparator.getClass().getName()));
				
			} else {
				logPreparator(preparator);
			}
			
			
			result.setSuccess(true);
		} catch (MailException ex) {
			LOGGER.error(String.format("Failed to send mail [%s]",preparator.getClass().getName()),ex);
			result.setErrorMessage(ex.getMessage());
			result.setSuccess(false);
		}
		return result;
	}



	public MailSendResult sendMail(List<String> recipients, String subject, String body,
			List<MailAttachment> attachments) {
		return sendMail(recipients.toArray(new String[recipients.size()]), subject, body, attachments);
	}

	@Override
	public MailSendResult sendMail(String[] recipients, String subject, String body, List<MailAttachment> attachments) {
		if(recipients == null) {
			throw new IllegalArgumentException("recipients parameter is mandatory!");
		}

		return sendMail(recipients, null, null, from, subject, body, attachments);
	}
	public MailSendResult sendMail(String[] to, String[] cc, String[] bcc,
			String subject, String body, List<MailAttachment> attachments) {
		return sendMail(to, cc, bcc, this.from, subject, body, attachments);
	}
	public MailSendResult sendMail(final String[] to, final String[] cc, final String[] bcc, 
			final String from,
			final String subject, 
			final String body, 
			final List<MailAttachment> attachments) {
		return sendMail(to,new MimeMessagePreparator() {
			
			@Override
			public void prepare(MimeMessage msg) throws Exception {
				addRecipient(msg, MimeMessage.RecipientType.TO, to);
				addRecipient(msg, MimeMessage.RecipientType.CC, cc);
				addRecipient(msg, MimeMessage.RecipientType.BCC, bcc);

				msg.setFrom(new InternetAddress(from));
				msg.setSubject(subject, "UTF-8");
				
				Multipart mp = new MimeMultipart();

		        MimeBodyPart mbp1 = new MimeBodyPart();
		        mbp1.setText(body, "UTF-8");;
		        mp.addBodyPart(mbp1);

				
				if (attachments!=null && attachments.size()>0) {
					for (MailAttachment att:attachments) {
						addAttachment(mp, att);
					}
				}
				
				msg.setContent(mp);
			}
		});
	}

	@Override
	public MailSendResult sendMail(String recipient, String subject, String body) {
		return sendMail(Arrays.asList(recipient), subject, body, null);
	}



	
	private static void addRecipient(MimeMessage mimeMessage, Message.RecipientType recipientType, String[] recipients) throws MessagingException {
		if(recipients != null) {
			for (String recipient : recipients) {
				mimeMessage.addRecipient(recipientType, new InternetAddress(recipient.trim()));
			}
		}
	}

	private static void addAttachment(Multipart multipart, MailAttachment attachment) throws MessagingException {
		DataSource source = new ByteArrayDataSource(attachment.getContent(), "application/octet-stream");
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(attachment.getFileName());
		multipart.addBodyPart(messageBodyPart);
	}
	
	private void logPreparator(MimeMessagePreparator preparator) {
		try {
			MimeMessage msg=new MimeMessage((Session)null);
			preparator.prepare(msg);
			StringBuilder sb=new StringBuilder();
			sb.append("From: ");
			Address[] from = msg.getFrom();
			appendAddressToLog(sb, from);
			sb.append("\n");
			
			sb.append("To: ");
			Address[] to = msg.getRecipients(RecipientType.TO);	
			appendAddressToLog(sb, to);
			sb.append("\n");
			
			sb.append("Subject: ");
			sb.append(msg.getSubject());
			sb.append("\n");
			
			sb.append("Content: ");
			sb.append(msg.getContent());
			sb.append("\n");
			
			LOGGER.info("Logging MIME message instead of sending:\n"+sb.toString());
		} catch (Exception e) {
			LOGGER.error("Failed to log MIME message",e);
		}
	}
	private static void appendAddressToLog(StringBuilder sb, Address[] from) {
		if (from!=null) {
			for (Address a:from) {
				sb.append(a.toString());	
				sb.append(' ');
			}
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getFrom() {
		return from;
	}

	public boolean isMailSendEnabled() {
		return mailSendEnabled;
	}
}
