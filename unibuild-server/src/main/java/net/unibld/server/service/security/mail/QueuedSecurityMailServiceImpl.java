package net.unibld.server.service.security.mail;

import java.util.List;
import java.util.Locale;

import net.unibld.server.entities.mail.MailQueueMessage;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.entities.security.invitation.InvitationStatus;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.entities.security.ticket.UserTicket.TicketType;
import net.unibld.server.service.mail.MailCallback;
import net.unibld.server.service.mail.MailQueueService;
import net.unibld.server.service.mail.MultiMailSendResult;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.service.security.SpringSecurityException;
import net.unibld.server.service.security.emailchange.EmailChangeService;
import net.unibld.server.service.security.invitation.InvitationService;
import net.unibld.server.service.security.ticket.UserTicketService;
import net.unibld.server.util.UrlConcat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

/**
 * An implementation of the {@link SecurityMailService} for user management related emails, using a database queue before sending
 * mails directly to SMTP.
 * @author andor
 *
 */
@Service("securityMailService")
public class QueuedSecurityMailServiceImpl implements SecurityMailService {
	private static final String EMAIL_ADDRESS_NOT_EXISTING = "E-mail address does not exist: ";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QueuedSecurityMailServiceImpl.class);

	
	@Value("${activation.url.base:'user/activate?ticket=%s'}")
	private String activationUrlBase;

	@Value("${activation.url.fallback:'user/activate.jsf'}")
	private String activationUrlFallback;
	
	
	@Value("${mail.smtp.from}")
	private String mailFrom;
	
	@Value("${mail.return.host}")
	private String returnHost;
	
	
	@Autowired
	private SecurityService securityService;
	@Autowired
	private UserTicketService userTicketService;
	@Autowired
	private MailQueueService mailQueueService;
	@Autowired
	private InvitationService invitationService;
	@Autowired
	private EmailChangeService emailChangeService;
	
	@Autowired
	@Qualifier("mailMessageSource")
	private ResourceBundleMessageSource messageSource;
	
	
	/**
	 * @param key Message key
	 * @param locale Locale to tranlate to
	 * @return Localized message using the locale specified
	 */
	private String getMessage(String key,String locale) {
		return messageSource.getMessage(key,null, new Locale(locale));
	}
	
	/**
	 * @param key Message key
	 * @param locale Locale to tranlate to
	 * @param args Var args for localization arguments in {0}, {1} etc. format
	 * @return Localized message using the locale specified
	 */
	private String getMessage(String key,String locale,String ... args) {
		return messageSource.getMessage(key,args, new Locale(locale));
	}
	
	
	private void sendActivationMail(UserProfile p) {
		UserTicket ticket = userTicketService.createTicket(p.getUserName(), TicketType.ACTIVATION);
		String text = getActivationMessageBody(p, ticket.getId());
		String subject=getActivationMessageSubject(p);
		queueMail(p.getEmail(),subject,text,ActivationMailCallback.class,p.getUserName());

		
	}

	

	protected String getActivationMessageSubject(UserProfile p) {
		String lang=p.getLanguage();
		if (lang==null) {
			lang=this.invitationService.getRegisterDefaultLanguage();
		}
		String siteName = getMessage("site.name", lang);
		
		return this.getMessage("mail.activationMail.subject",lang,siteName);
	}


	protected String getActivationMessageBody(UserProfile p, String ticket) {
		
		String activationUrl = UrlConcat.concatenateUrlParts(returnHost,(String.format(activationUrlBase,ticket)));
		String fallbackUrl = UrlConcat.concatenateUrlParts(returnHost,(activationUrlFallback));
		
		String lang=p.getLanguage();
		if (lang==null) {
			lang=this.invitationService.getRegisterDefaultLanguage();
		}
		String siteName = this.getMessage("site.name", lang);
		String siteUrl = this.getMessage("site.url", lang);
	
		String text = this.getMessage("mail.activationMail.body", lang, 
				//0: full name
				p.getFirstName()!=null?p.getFirstName():p.getUserName(),
				//1: username
				p.getUserName(),
				//2: email
				p.getEmail(),
				//3: activation link
				activationUrl,
				//4: activation fallback url
				fallbackUrl,
				//5: activation fallback code
				ticket,
				//6: mailto
				mailFrom,
				//7: footer
				this.getMessage("mail.footer", lang,siteName,siteUrl));
		return text;
	}



	


	private void sendPasswordChangeMail(String email,boolean forgotten)
		throws SpringSecurityException {
		List<UserProfile> p = securityService.getUserProfileByEmail(email);
		if (p==null||p.size()==0) {
			throw new SpringSecurityException(EMAIL_ADDRESS_NOT_EXISTING+email);
		}
		
		UserProfile user=p.get(0);
		
		if (user==null) {
			throw new SpringSecurityException(EMAIL_ADDRESS_NOT_EXISTING+email);
		}
		String ticket = userTicketService.createTicket(user.getUserName(), TicketType.PASSWORD_CHANGE).getId();
		
		String text = getChangePasswordMessageBody(user,ticket, forgotten);
		String subject = getChangePasswordMessageSubject(user, forgotten);
		queueMail(email, subject, text,null,null);
	}


	protected String getChangePasswordMessageSubject(UserProfile user,
			boolean forgotten) {
		String lang=user.getLanguage();
		if (lang==null) {
			lang=this.invitationService.getRegisterDefaultLanguage();
		}
		String siteName = getMessage("site.name", lang);
		
		String subjectKey = forgotten?"mail.forgottenPasswordMail.subject":"mail.passwordChangeMail.subject";
		String subject=this.getMessage(subjectKey, lang,siteName);
		return subject;
	}


	protected String getChangePasswordMessageBody(UserProfile profile,String ticket,
			boolean forgotten) {
		
		String pwChangeUrl = UrlConcat.concatenateUrlParts(returnHost,("/passwordchange.xhtml?code="+ticket));
		String fallbackUrl = UrlConcat.concatenateUrlParts(returnHost,("/passwordchange.xhtml")); 
		
		String lang=profile.getLanguage();
		if (lang==null) {
			lang=this.invitationService.getRegisterDefaultLanguage();
		}
		
		String siteName = this.getMessage("site.name", lang);
		String siteUrl = this.getMessage("site.url", lang);
		
		String bodyKey = forgotten?"mail.forgottenPasswordMail.body":"mail.passwordChangeMail.body";
		String text = this.getMessage(bodyKey, lang, 
				//0: full name
				profile.getFirstName()!=null?profile.getFirstName():profile.getUserName(),
				//1: user name
				profile.getUserName(),
				//2: email
				profile.getEmail(),
				//3: activation link
				pwChangeUrl,
				//4: activation fallback url
				fallbackUrl,
				//5: activation fallback code
				ticket,
				//6: mailto
				mailFrom,
				//7: footer
				this.getMessage("mail.footer", lang,siteName,siteUrl));
		return text;
	}

	
	public MultiMailSendResult sendActivationMails() {
		List<UserProfile> users = securityService.getUsersToNotifyAboutActivation();
		MultiMailSendResult r=new MultiMailSendResult();
		for (UserProfile p : users) {
			try {
				sendActivationMail(p);
				r.incSent();
			} catch (Exception e) {
				LOGGER.error("Failed to send activation mail to: " + p.getUserName(),e);
				r.incFailed();
			}
		}
		return r;
	}

	public void sendActivationMail(String userName) throws SpringSecurityException {
		UserProfile p = securityService.findUserProfile(userName);
		if (p==null) {
			throw new SpringSecurityException("User profile not found: "+userName);
		}
		
		if (p.isActivated()) {
			throw new SpringSecurityException("User is already activated: "+userName);
		}
		
		
		try {
			sendActivationMail(p);
		} catch (Exception e) {
			LOGGER.error("Failed to send activation mail to: " + userName,e);
		}
	}

	public void sendEmailChangeMail(UserProfile profile,String newEmail) {
		UserTicket t = userTicketService.createTicket(profile.getUserName(), TicketType.EMAIL_CHANGE);

		emailChangeService.createEmailChangeRequest(t, newEmail);
		
		String body=getEmailChangeMessageBody(profile, t.getId(),newEmail);
		String subject=getEmailChangeMailSubject(profile);
		queueMail(newEmail,subject, body, null, null);
	}

	protected String getEmailChangeMessageBody(UserProfile profile,String ticket,
			String newEmail) {
		
		String emailChangeUrl = UrlConcat.concatenateUrlParts(returnHost,("/email/change/validate/"+ticket));
		String fallbackUrl = UrlConcat.concatenateUrlParts(returnHost,("/email/change/validate"));
		String lang=profile.getLanguage();
		if (lang==null) {
			lang=this.invitationService.getRegisterDefaultLanguage();
		}
		
		String siteName = getMessage("site.name", lang);
		String siteUrl = getMessage("site.url", lang);
		
		String text = getMessage("mail.emailChangeMail.body", lang, 
				//0: full name
				profile.getFirstName()!=null?profile.getFirstName():profile.getUserName(),
				//1: user name
				profile.getUserName(),
				//2: current email
				profile.getEmail(),
				//3: new email
				newEmail,
				//4: link
				emailChangeUrl,
				//5: fallback url
				fallbackUrl,
				//6: fallback code
				ticket,
				//7: mailto
				mailFrom,
				//8: footer
				getMessage("mail.footer", lang,siteName,siteUrl));
		return text;
	}

	protected String getEmailChangeMailSubject(UserProfile profile) {
		String lang=profile.getLanguage();
		if (lang==null) {
			lang=this.invitationService.getRegisterDefaultLanguage();
		}
		String siteName = getMessage("site.name", lang);
		
		return getMessage("mail.emailChangeMail.subject", lang, siteName);
	}
	
	public void sendActivatedMail(UserProfile p) {
		
		String body = getActivatedMailBody(p);
		String subject=getActivatedMailSubject(p);
		queueMail(p.getEmail(),subject,body,null,null);

		LOGGER.info("Activated e-mail queued to user: "+p.getUserName());
	}

	protected String getActivatedMailBody(UserProfile profile) {
		
		String loginUrl = UrlConcat.concatenateUrlParts(returnHost,("index"));
		
		String lang=profile.getLanguage();
		if (lang==null) {
			lang=this.invitationService.getRegisterDefaultLanguage();
		}
		
		String siteName = getMessage("site.name", lang);
		String siteUrl = getMessage("site.url", lang);
		
		String text = getMessage("mail.activatedMail.body", lang, 
				//0: full name
				profile.getFirstName()!=null?profile.getFirstName():profile.getUserName(),
				//1: username
				profile.getUserName(),
				//2: login url
				loginUrl,
				//3: mailto
				mailFrom,
				//4: footer
				getMessage("mail.footer", lang,siteName,siteUrl));
		return text;
	}

	protected String getActivatedMailSubject(UserProfile p) {
		String lang=p.getLanguage();
		if (lang==null) {
			lang=this.invitationService.getRegisterDefaultLanguage();
		}
		String siteName = getMessage("site.name", lang);
		
		return getMessage("mail.activatedMail.subject",lang,siteName);
	}
	
	

	public void sendForgottenPasswordMail(String email) throws SpringSecurityException {
		sendPasswordChangeMail(email, true);
	}

	public void sendPasswordChangeMail(String email) throws SpringSecurityException {
		sendPasswordChangeMail(email,false);

	}
	
	protected void queueMail(String email,String subject,String body,Class<? extends MailCallback> callbackClass,String callbackParam) {
		MailQueueMessage msg = new MailQueueMessage();
		msg.setBody(body);
		msg.setSubject(subject);
		msg.setTo(email);
		if (callbackClass!=null) {
			msg.setCallbackClass(callbackClass.getName());
		}
		msg.setCallbackParameter(callbackParam);
		mailQueueService.addToMailQueue(msg);
		LOGGER.info("Added security mail to mail queue: To:{} Subject:{}",email,subject);
	}
	

	public void sendInvitationMails() {
		List<Invitation> users = invitationService.getInvitationsActive();
		for (Invitation p : users) {
			try {
				sendInvitationMail(p);
				
			} catch (Exception e) {
				LOGGER.error("Failed to send invitation mail to: " + p.getEmail(),e);
			}
		}
	
	}
	public void sendInvitationMail(Invitation inv) throws SpringSecurityException {
		if (inv.getStatus()==InvitationStatus.DELETED) {
			throw new SpringSecurityException("This invitation has already been used: "+inv.getId());
		}
		
		String lang=this.invitationService.getRegisterDefaultLanguage();
		String body=getInvitationMessageBody(inv, lang);
	
		String subject=getInvitationMailSubject(lang);
		queueMail(inv.getEmail(),subject,body,InvitationMailCallback.class,String.valueOf(inv.getId()));

		inv=invitationService.updateInvitationQueued(inv);
		LOGGER.info("Invitation mail sent: "+inv.getEmail());
	}

	protected String getInvitationMessageBody(Invitation inv, String lang) {
		
		String activationUrl = UrlConcat.concatenateUrlParts(returnHost,String.format("activate.xhtml?code=%s",inv.getAuthCode()));
		String fallbackUrl = UrlConcat.concatenateUrlParts(returnHost,("activate.xhtml"));
		
		
		String text = null;
		
		UserProfile invitor = inv.getInvitor();
		
		String siteName = getMessage("site.name", lang);
		String siteUrl = getMessage("site.url", lang);
		String siteCompany = getMessage("site.company", lang);
		text=getMessage("mail.invitationMail.body", lang, 
			//0: invitor full name
			invitor.getFirstName()!=null?invitor.getFirstName():invitor.getUserName(),
			//1: site name
			siteName,
			//2: invitation accept link
			activationUrl,
			//3: invitation accept fallback url
			fallbackUrl,
			//4: invitation fallback code
			inv.getAuthCode(),
			//5: mailto
			mailFrom,
			//6: footer
			getMessage("mail.footer", lang,siteCompany,siteUrl));
		return text;
	}

	protected String getInvitationMailSubject(String lang) {
		String siteName = getMessage("site.name", lang);
		
		return getMessage("mail.invitationMail.subject",lang,siteName);
	}

	
	
	
	 
}
