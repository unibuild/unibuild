package net.unibld.server.entities.mail;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.unibld.core.mail.MailAttachment;

@Entity
@Table(name = "mail_queue_attachment")
public class MailQueueAttachment implements MailAttachment {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_message", referencedColumnName = "id", insertable = true, updatable = false, nullable = false)
	private MailQueueMessage message;

	@Column(name="content",nullable=false)
	@Lob
	private byte[] content;
	
	
    @Column(name = "file_name", nullable = true,length=255)
    private String fileName;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public MailQueueMessage getMessage() {
		return message;
	}


	public void setMessage(MailQueueMessage message) {
		this.message = message;
	}


	public byte[] getContent() {
		return content;
	}


	public void setContent(byte[] data) {
		this.content = data;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


}
