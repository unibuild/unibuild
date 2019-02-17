package net.unibld.server.web.jsf.model;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.UserProfile;

public class UserEditModel {
	private UserProfile user;
	private Authority role;
	
	private String selectedRoleName;
	
	private String password;
	private String passwordAgain;
	
	public UserProfile getUser() {
		return user;
	}
	public void setUser(UserProfile user) {
		this.user = user;
		
		
	}
	
	public void setRole(Authority role) {
		this.role = role;
		if (role==null) {
			selectedRoleName=null;
		} else {
			selectedRoleName=role.getAuthority();
			
		}
	}
	

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordAgain() {
		return passwordAgain;
	}
	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}
	public String getSelectedRoleName() {
		return selectedRoleName;
	}
	public void setSelectedRoleName(String selectedRoleName) {
		this.selectedRoleName = selectedRoleName;
	}
	public Authority getRole() {
		return role;
	}
}
