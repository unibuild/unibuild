package net.unibld.server.entities.security.invitation;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.UserProfile;



/**
 * An entity representing an invitation sent to a future {@link Player}, by a captain
 * of a {@link Team}
 * @author andor
 *
 */
@Entity
@Table(name = "invitation")
public class Invitation implements Serializable{

	private static final long serialVersionUID = -5342969525222857888L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;

	@Column(name = "email",length=100,nullable=false)
	private String email;
	
	@Column(name = "auth_code",length=255,nullable=false)
	private String authCode;
	
	@ManyToOne(cascade=CascadeType.REFRESH,fetch=FetchType.EAGER)
	@JoinColumn(name="id_profile", referencedColumnName = "user_name", insertable = true, updatable = true,nullable=false)
	private UserProfile invitor;
	
	
	@Column(name = "invitation_status",length=20,nullable=false)
	@Enumerated(EnumType.STRING)
	private InvitationStatus status=InvitationStatus.ACTIVE;

	@Column(name = "invited_group",length=20,nullable=true)
	@Enumerated(EnumType.STRING)
	private Group invitedGroup;
	
	@Column(name="create_date",nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Column(name="mod_date",nullable = false, updatable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modDate;
	
	@Column(name="send_date",nullable = true, updatable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendDate;
	
	@Column(name = "creator",length=50,nullable=false)
	private String creator;
	@Column(name = "modifier",length=50,nullable=false)
	private String modifier;
	
	@PrePersist
	public void prePersist() {
		this.createDate=new Date();
		this.modDate=createDate;
	}
	
	@PreUpdate
	public void preUpdate() {
		this.modDate=new Date();
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}





	


	/**
	 * @return Invited player e-mail
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * @param email Invited player e-mail
	 */
	public void setEmail(String email) {
		this.email = email;
	}








	public InvitationStatus getStatus() {
		return status;
	}








	public void setStatus(InvitationStatus status) {
		this.status = status;
	}








	public String getAuthCode() {
		return authCode;
	}



	public boolean isCancellable() {
		return status==InvitationStatus.ACTIVE||status==InvitationStatus.SENT;
	}
	public boolean isResendable() {
		return status!=InvitationStatus.USED;
	}



	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}








	public UserProfile getInvitor() {
		return invitor;
	}








	public void setInvitor(UserProfile invitor) {
		this.invitor = invitor;
	}








	public Group getInvitedGroup() {
		return invitedGroup;
	}








	public void setInvitedGroup(Group invitedGroup) {
		this.invitedGroup = invitedGroup;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}








	
}
