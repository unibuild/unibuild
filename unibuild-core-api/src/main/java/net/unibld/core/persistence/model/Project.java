package net.unibld.core.persistence.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * An entity that represents a project in the UniBuild system.<br>
 * Each project can have several runnable goals that are sequences of several tasks to execute.
 * @author andor
 *
 */
@Entity
@Table(name="project")
public class Project implements Serializable {
	
	private static final long serialVersionUID = 3952591017474464145L;

	@Id
	@Column(nullable=false)
	private String id;

	@Column(nullable=false,unique=true)
	private String path;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=true)
	private String displayName;
	
	@Column(nullable=true)
	private String creatorUser;

	@Column(nullable=true)
	private String lastModifierUser;

	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;
	
	@Column(nullable=false)
	private boolean deleted;
	
	@Column(nullable=false)
	private int buildNumber;
	
	/**
	 * Method invoked on JPA event {@link PrePersist}
	 */
	@PrePersist
	public void prePersist() {
		this.createDate=new Date();
		this.lastModifiedDate=createDate;
	}
	/**
	 * Method invoked on JPA event {@link PreUpdate}
	 */
	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate=new Date();
	}
	
	
	/**
	 * @return Globally unique ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id Globally unique ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Path of the project file (project.xml)
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path Path of the project file (project.xml)
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return Name of the project
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Name of the project
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Display name of the project
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName Display name of the project
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return Creator user ID
	 */
	public String getCreatorUser() {
		return creatorUser;
	}

	/**
	 * @param creatorUser Creator user ID
	 */
	public void setCreatorUser(String creatorUser) {
		this.creatorUser = creatorUser;
	}

	/**
	 * @return Last modifier user ID
	 */
	public String getLastModifierUser() {
		return lastModifierUser;
	}

	/**
	 * @param lastModifierUser Last modifier user ID
	 */
	public void setLastModifierUser(String lastModifierUser) {
		this.lastModifierUser = lastModifierUser;
	}

	/**
	 * @return Creation time
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate Creation time
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return Last modification time
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate Last modification time
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return True if the project is deleted.
	 */ 
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted True if the project is deleted
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return The current build number of the project
	 */
	public int getBuildNumber() {
		return buildNumber;
	}

	/**
	 * @param buildNumber The current build number of the project
	 */
	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	@Override
	public String toString() {
		return String.format("%s [%s]",name,path);
	}
	

}
