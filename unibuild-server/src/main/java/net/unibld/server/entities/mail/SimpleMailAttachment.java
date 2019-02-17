package net.unibld.server.entities.mail;

import net.unibld.core.mail.MailAttachment;

public class SimpleMailAttachment implements MailAttachment {
	public SimpleMailAttachment(String fileName, byte[] content) {
		super();
		this.fileName = fileName;
		this.content = content;
	}
	
	
	private String fileName;
	
	private byte[] content;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
}
