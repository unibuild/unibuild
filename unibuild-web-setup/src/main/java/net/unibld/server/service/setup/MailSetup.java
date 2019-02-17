package net.unibld.server.service.setup;

import net.unibld.server.service.setup.smtp.SmtpDefaults;

public class MailSetup {
	private String host=SmtpDefaults.DEFAULT_HOST;
	private int port=SmtpDefaults.DEFAULT_PORT;
	private String user;
	private String password;
	private String senderEmail;
	
	private boolean startTls;
	private boolean ssl;
	
	private String returnHost;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSenderEmail() {
		return senderEmail;
	}
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	public boolean isStartTls() {
		return startTls;
	}
	public void setStartTls(boolean startTls) {
		this.startTls = startTls;
	}
	public String getReturnHost() {
		return returnHost;
	}
	public void setReturnHost(String returnHost) {
		this.returnHost = returnHost;
	}
	public boolean isSsl() {
		return ssl;
	}
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
}
