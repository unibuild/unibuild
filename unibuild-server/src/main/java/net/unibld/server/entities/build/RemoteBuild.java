package net.unibld.server.entities.build;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.server.entities.security.UserProfile;

/**
 * An entity describing a remote build process.
 * @author andor
 *
 */
@Entity
@Table(name = "remote_build")
public class RemoteBuild {
	/**
	 * Globally unique ID
	 */
	@Id
	@Column(nullable=false)
	private String id;

	/**
	 * Related project
	 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id", referencedColumnName = "id", insertable = true, updatable = false, nullable = false)
	private Project project;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "build_id", referencedColumnName = "id", insertable = true, updatable = true, nullable = true)
	private Build build;
	
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_name", referencedColumnName = "user_name", insertable = true, updatable = false, nullable = false)
	private UserProfile user;

	@Column(name="create_date",nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Column(name="complete_date",nullable = true, updatable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date completedDate;
	
	@Column(name = "goal", length = 30, nullable = true)
	private String goal;
	
	@Column(name="status",length=30,nullable=false)
	@Enumerated(EnumType.STRING)
	private RemoteBuildStatus status;
	
	@Column(name = "error_message", length = 255, nullable = true)
	private String errorMessage;
	
	
	@PrePersist
	public void prePersist() {
		this.createDate=new Date();
		this.status=RemoteBuildStatus.STARTED;
	}

	public Project getProject() {
		return project;
	}


	public void setProject(Project project) {
		this.project = project;
	}


	public UserProfile getUser() {
		return user;
	}


	public void setUser(UserProfile user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public RemoteBuildStatus getStatus() {
		return status;
	}

	public void setStatus(RemoteBuildStatus status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}



}
