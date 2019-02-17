package net.unibld.server.service.security;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.ticket.UserTicket;

public class UserItem {
	private UserProfile profile;
	private Authority authority;
	private UserTicket accessTicket;
	public UserProfile getProfile() {
		return profile;
	}
	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}
	public Authority getAuthority() {
		return authority;
	}
	public void setAuthority(Authority authority) {
		this.authority = authority;
	}
	
	public Group getGroup() {
		if (authority==null||authority.getAuthority()==null) {
			return null;
		}
		return Group.valueOf(authority.getAuthority());
	}
	public void setGroup(Group g) {
		if (g==null) {
			this.authority=null;
		} else {
			if (this.authority==null) {
				this.authority=new Authority();
			}
			this.authority.setAuthority(g.name());;
		}
		
	}
	public String getUserAuthority() {
		if (authority==null) {
			return null;
		}
		return authority.getAuthority();
	}
	
	public boolean isAdmin() {
		Group g = getGroup();
		if (g==null) {
			return false;
		}
		return g==Group.ROLE_ADMIN;
	}
	public UserTicket getAccessTicket() {
		return accessTicket;
	}
	public void setAccessTicket(UserTicket accessTicket) {
		this.accessTicket = accessTicket;
	}
}
