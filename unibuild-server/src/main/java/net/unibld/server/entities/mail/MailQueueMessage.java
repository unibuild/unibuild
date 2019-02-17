package net.unibld.server.entities.mail;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "mail_queue")
public class MailQueueMessage {
	@Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    private Long id;
	
	@Column(name = "to_address",length=512, nullable=true)
	private String to;
	
	@Column(name = "cc_address",length=512, nullable=true)
	private String cc;
	
	@Column(name = "bcc_address",length=512, nullable=true)
	private String bcc;
	
	@Column(name = "subject",length=512, nullable=false)
	private String subject;
	
	@Column(name = "body",nullable=false)
	@Lob
	private String body;
	
	
	

    
    /**
     * Küldés indításának időpontja
     */
    @Column(name = "send_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendDate;

   
    
	
   
    
	/**
     * Módosítás időpontja
     */
    @Column(name = "moddate",nullable=false)
    private Date modDate;
	
    /**
     * Létrehozás időpontja
     */
    @Column(name = "createdate",nullable=false)
    private Date createDate;
    
    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private MailQueueStatus status;
    
    @Column(name = "failure_reason", nullable=true)
    @Lob
    private String failureReason;
    
    @Column(name = "callback_class", length = 255, nullable=true)
    private String callbackClass;
    @Column(name = "callback_parameter", length = 255, nullable=true)
    private String callbackParameter;
    
    @PrePersist
    public void prePersist() {
    	this.createDate = new Date();
        this.modDate = new Date();
        this.status=MailQueueStatus.WAITING_FOR_SEND;
    }
    @PreUpdate
    public void preUpdate() {
        this.modDate = new Date();
    }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	
	public Date getModDate() {
		return modDate;
	}
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public MailQueueStatus getStatus() {
		return status;
	}
	public void setStatus(MailQueueStatus status) {
		this.status = status;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	
	public String getCallbackClass() {
		return callbackClass;
	}
	public void setCallbackClass(String callbackClass) {
		this.callbackClass = callbackClass;
	}
	public String getCallbackParameter() {
		return callbackParameter;
	}
	public void setCallbackParameter(String callbackParameter) {
		this.callbackParameter = callbackParameter;
	}
	
}
