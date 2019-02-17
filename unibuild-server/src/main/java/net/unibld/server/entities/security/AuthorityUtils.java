package net.unibld.server.entities.security;

import org.springframework.security.core.GrantedAuthority;

public class AuthorityUtils {

	public static Authority fromGrantedAuthority(User user,GrantedAuthority ga) {
		Authority a=new Authority();
		a.setAuthority(ga.getAuthority());
		a.setUser(user);
		return a;
	}

}
