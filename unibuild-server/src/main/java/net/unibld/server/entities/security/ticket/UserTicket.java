package net.unibld.server.entities.security.ticket;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * An entity that provides secure tickets for operations that require authentication but no
 * HTTP or Flex session is available (because of circumstances such as asynchronous execution
 * or using separate communication channels for different operations of a user) 
 * @author andor
 *
 */
@Entity
@Table(name="user_ticket")
public class UserTicket implements Serializable {
	
	private static final long serialVersionUID = 1696885963188960608L;
	/**
	 * User ticket type enum
	 * @author andor
	 *
	 */
	public enum TicketType {
		/**
		 * Ticket for user activation after registration
		 */
		ACTIVATION, 
		/**
		 * Ticket for external authentication
		 */
		AUTHENTICATION,
		/**
		 * Ticket for password change
		 */
		PASSWORD_CHANGE,
		FILE_UPLOAD,
		FILE_DOWNLOAD, 
		/**
		 * Ticket for email change
		 */
		EMAIL_CHANGE,
		/**
		 * Ticket for remote access
		 */
		REMOTE_ACCESS;
	}
	
	public enum TicketExpiration {
		TEMPORARY,
		PERSISTENT
	}
	@Id
	@Column(name="id",length=255,nullable=false,unique=true)
    private String id;
	
	@Column(name="user_name",length=50,nullable=false)
    private String userName;
	
	@Column(name="create_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Column(name="ticket_type",length=50,nullable=false)
	@Enumerated(EnumType.STRING)
	private TicketType type;
	
	@Column(name="ticket_expiration",length=50,nullable=false)
	@Enumerated(EnumType.STRING)
	private TicketExpiration expiration;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	/**
	 * @return the type
	 */
	public TicketType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(TicketType type) {
		this.type = type;
	}
	/**
	 * @param currentTime Current time in msecs
	 * @param expiryInSecs Expiry in secs
	 * @return True if this ticket is valid
	 */
	public boolean isValid(long currentTime, int expiryInSecs) {
		if (expiration==TicketExpiration.PERSISTENT) {
			return true;
		}
		double d=currentTime-this.createDate.getTime();
		
		return d-expiryInSecs*1000<=0;
	}
	public TicketExpiration getExpiration() {
		return expiration;
	}
	public void setExpiration(TicketExpiration expiration) {
		this.expiration = expiration;
	}
	
}
