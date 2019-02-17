package net.unibld.core.task.impl.sys;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.unibld.core.build.BuildException;
import net.unibld.core.mail.EmailService;
import net.unibld.core.mail.MailSendResult;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;


/**
 * A task runner for {@link MailTask}, that sends a mail using the mail account configured in
 * global config.
 * @author andor
 *
 */
@Component
public class MailTaskRunner extends BaseTaskRunner<MailTask> {
	private static final Logger LOG=LoggerFactory.getLogger(MailTaskRunner.class);
	
	@Autowired
	private EmailService emailService;
	
	protected ExecutionResult execute(MailTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		if (StringUtils.isEmpty(emailService.getHost())) {
			throw new BuildException("SMTP host not configured.");
		}
		if (StringUtils.isEmpty(emailService.getFrom())) {
			throw new BuildException("SMTP sender not configured.");
		}
		if (!emailService.isMailSendEnabled()) {
			throw new BuildException("Mail send is disabled.");
		}
		
		
		if (StringUtils.isEmpty(task.getTo())&&StringUtils.isEmpty(task.getCc())&&
				StringUtils.isEmpty(task.getBcc())) {
			throw new BuildException("At least one recipient is required");
		}
		
		if (StringUtils.isEmpty(task.getSubject())) {
			throw new BuildException("Subject is empty");
		}
		if (StringUtils.isEmpty(task.getBody())) {
			throw new BuildException("Body is empty");
		}
		
		
		StringBuilder allRecipients=new StringBuilder();
		List<String> toList=new ArrayList<String>();
		List<String> ccList=new ArrayList<String>();
		List<String> bccList=new ArrayList<String>();
		
		int rec=0;
		
		EmailValidator emailValidator = EmailValidator.getInstance();
		
		
		if (task.getTo()!=null) {
			String[] toSpl = task.getTo().split(",");
			
			if (toSpl.length>0) {
				for (String to:toSpl) {
					if (!emailValidator.isValid(to.trim())) {
						throw new BuildException("Invalid email address: "+to.trim());
					}
					toList.add(to.trim());
					if (rec>0) {
						allRecipients.append(',');
					}
					allRecipients.append(to.trim());
					rec++;
				}
			} 
		}
		
		if (task.getCc()!=null) {
			String[] ccSpl = task.getCc().split(",");
			
			if (ccSpl.length>0) {
				for (String cc:ccSpl) {
					if (!emailValidator.isValid(cc.trim())) {
						throw new BuildException("Invalid email address: "+cc.trim());
					}
					ccList.add(cc.trim());
					if (rec>0) {
						allRecipients.append(',');
					}
					allRecipients.append(cc.trim());
					rec++;
				}
			} 
		}
		
		if (task.getBcc()!=null) {
			String[] bccSpl = task.getBcc().split(",");
			
			if (bccSpl.length>0) {
				for (String bcc:bccSpl) {
					if (!emailValidator.isValid(bcc.trim())) {
						throw new BuildException("Invalid email address: "+bcc.trim());
					}
					bccList.add(bcc.trim());
					if (rec>0) {
						allRecipients.append(',');
					}
					allRecipients.append(bcc.trim());
					rec++;
				}
			} 
		}
		logTask("Sending mail {0} to: {1}...", task.getSubject(), allRecipients.toString());
		
		
		try {
			MailSendResult result = emailService.sendMail(toList.toArray(new String[toList.size()]), 
					ccList.toArray(new String[ccList.size()]), 
					bccList.toArray(new String[bccList.size()]), 
					task.getSubject(), task.getBody(), null);
			if (result.isSuccess()) {
				return ExecutionResult.buildSuccess();
			} else {
				return ExecutionResult.buildError("Failed to send email: "+result.getErrorMessage());
			}
		} catch (Exception ex) {
			LOG.error("Failed to send email",ex);
			return ExecutionResult.buildError("Failed to send email",ex);
		}
	}


	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "mail";
	}
}