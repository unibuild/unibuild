package net.unibld.core.task.impl.sys;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;

/**
 * A task that sends a mail using the mail account configured in global config.
 * @author andor
 *
 */
@Task(name="mail",runnerClass=MailTaskRunner.class)
public class MailTask extends BuildTask {
	
	private static final long serialVersionUID = 4743090891731302131L;
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String body;
	
	/**
	 * @return Comma separated list of to addresses
	 */
	public String getTo() {
		return to;
	}
	/**
	 * @param to Comma separated list of to addresses
	 */
	public void setTo(String to) {
		this.to = to;
	}
	/**
	 * @return Comma separated list of cc addresses
	 */
	public String getCc() {
		return cc;
	}
	/**
	 * @param cc Comma separated list of cc addresses
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}
	/**
	 * @return Comma separated list of bcc addresses
	 */
	public String getBcc() {
		return bcc;
	}
	/**
	 * @param bcc Comma separated list of bcc addresses
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	/**
	 * @return Mail subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject Mail subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return Mail body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body Mail body
	 */
	public void setBody(String body) {
		this.body = body;
	}
}
