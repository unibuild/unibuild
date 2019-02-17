package net.unibld.server.entities.security;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;




/**
 * An entity that contains profile info of a spring user
 * 
 * @author andor
 * 
 */
@Entity
@Table(name = "user_profile")
public class UserProfile implements Serializable,Cloneable {
	private static final long serialVersionUID = -4657187386012473901L;
	
	
	@Id
	@Column(name="user_name",length=50)
	private String userName;
	
	@Column(name="email",length=255,nullable=false)
	private String email;
	
	@Column(name="phone_number",length=50,nullable=true)
	private String phoneNumber;
	
	@Column(name="last_login_platform",length=30,nullable=true)
	private String lastLoginPlatform;
	
	

	@Column(name="language",length=4,nullable=true)
	private String language;
	
	
	@Column(name="first_name",length=100,nullable=true)
	private String firstName;
	
	@Column(name="last_name",length=100,nullable=true)
	private String lastName;
	
	@Column(name="is_activated",length=1,nullable=false)
	private boolean activated=false;


	@Column(name="is_activation_mail_sent",length=1,nullable=false)
	private boolean activationMailSent=false;
	

	@Column(name="is_email_verified",length=1,nullable=false)
	private boolean emailVerified=false;
	
	@Column(name="last_login",nullable = false, updatable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;
	
	@Column(name="reg_date",nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date registerDate;
	
	@Column(name="user_status",nullable = false, updatable = true)
	@Enumerated(EnumType.STRING)
	private UserStatus status=UserStatus.INACTIVE;

	
	
	@Column(name="description",nullable=true)
	@Lob
	private String description;
	

	@PrePersist
	public void prePersist() {
		this.registerDate=new Date();
	}
	

	
	

	/**
	 * @return the activated
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * @param activated the activated to set
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	/**
	 * @return the activationMailSent
	 */
	public boolean isActivationMailSent() {
		return activationMailSent;
	}

	/**
	 * @param activationMailSent the activationMailSent to set
	 */
	public void setActivationMailSent(boolean activationMailSent) {
		this.activationMailSent = activationMailSent;
	}

	/**
	 * @return the lastLogin
	 */
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin the lastLogin to set
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	
	public UserProfile clone() throws CloneNotSupportedException {
		return (UserProfile) super.clone();
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the registerDate
	 */
	public Date getRegisterDate() {
		return registerDate;
	}

	/**
	 * @param registerDate the registerDate to set
	 */
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	/**
	 * @return the status
	 */
	public UserStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(UserStatus status) {
		this.status = status;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}
	
	public String getLastLoginPlatform() {
		return lastLoginPlatform;
	}

	public void setLastLoginPlatform(String lastLoginPlatform) {
		this.lastLoginPlatform = lastLoginPlatform;
	}



}
