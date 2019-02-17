package net.unibld.server.entities.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.unibld.server.entities.security.ticket.UserTicket;

/**
 * A request from a user to change his or her email to another one, using a {@link UserTicket} and
 * email validation.
 * @author andor
 *
 */
@Entity
@Table(name = "email_change_request")
public class EmailChangeRequest implements Serializable {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
	
	
	@Column(name="ticket",length=255,nullable=false)
    private String ticket;
		
	@Column(name="email",length=255,nullable=false)
	private String email;
	
	
	@Column(name="user_id", nullable=false)
	private String userId;
	
	
	
	public long getId() {
		if (id==null) {
			return 0;
		}
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
		
}
