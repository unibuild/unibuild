package net.unibld.server.entities.security;

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
import javax.persistence.Table;

import net.unibld.core.persistence.model.Project;
import net.unibld.core.persistence.model.ProjectAccessLevel;

/**
 * An entity describing user rights for projects.
 * @author andor
 *
 */
@Entity
@Table(name = "project_user_right")
public class ProjectUserRight {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	/**
	 * Related project
	 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id", referencedColumnName = "id", insertable = true, updatable = false, nullable = false)
	private Project project;
	
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_name", referencedColumnName = "user_name", insertable = true, updatable = false, nullable = false)
	private UserProfile user;
	
	@Column(name="access_level",length=30,nullable=false)
	@Enumerated(EnumType.STRING)
	private ProjectAccessLevel accessLevel;

	public String getProjectId() {
		return project.getId();
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
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


	public ProjectAccessLevel getAccessLevel() {
		return accessLevel;
	}


	public void setAccessLevel(ProjectAccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}



}
