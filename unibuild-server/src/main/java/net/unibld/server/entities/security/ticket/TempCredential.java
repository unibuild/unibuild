package net.unibld.server.entities.security.ticket;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A temporary credential object for no-password authentication in cases such as activation with isActivationPasswordAskEnabled = true.<br>
 * This situation occurs if a user must be created without a user specified password, so the user is created with an automatic password which
 * must be changed on activation. The {@link TempCredential} record itself should be deleted as well after a successful activation.<br>
 * In cases when the user specifies a password prior to registering to Spring Security, {@link TempCredential}s should not be used.
 * @author andor
 *
 */
@Entity
@Table(name="temp_credential")
public class TempCredential implements Serializable {
	 
	
	private static final long serialVersionUID = 8324795262397204532L;

	@Id
	@Column(name="user_name",length=50,nullable=false)
    private String userName;
	
	@Column(name="password",length=50,nullable=false)
	private String password;

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

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
}
