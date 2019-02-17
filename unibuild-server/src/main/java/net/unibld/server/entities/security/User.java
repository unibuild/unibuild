package net.unibld.server.entities.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User implements UserDetails {
	@Id
	@Column(name="username",length=100, nullable=false)
	private String username;

	@Column(name = "password", length = 100, nullable = false)
	private String password;
	@Column(name = "enabled", length = 1, nullable = false)
	private boolean enabled;
	
	@OneToMany(targetEntity = Authority.class, mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	private List<Authority> authorities;

	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	public Collection getAuthorities() {
		return (Collection<? extends GrantedAuthority>)authorities;
	}

	public boolean isAccountNonExpired() {
		return enabled;
	}

	public boolean isAccountNonLocked() {
		return enabled;
	}

	public boolean isCredentialsNonExpired() {
		return enabled;
	}
	
	public void addAuthority(Authority auth) {
		if (authorities==null) {
			authorities=new ArrayList<Authority>();
		}
		authorities.add(auth);
	}

	public void addAuthorities(List<Authority> list) {
		if (authorities==null) {
			authorities=new ArrayList<Authority>();
		}
		authorities.addAll(list);
	} 
	
	public void addGrantedAuthorities(List<GrantedAuthority> list) {
		if (authorities==null) {
			authorities=new ArrayList<Authority>();
		}
		
		for (GrantedAuthority ga:list) {
			if (ga instanceof Authority) {
				authorities.add((Authority) ga);
			} else if (ga instanceof SimpleGrantedAuthority) {
				authorities.add(AuthorityUtils.fromGrantedAuthority(this,ga));
			}
		}
	} 
	
	public List<Authority> getAuthorityList() {
		return authorities;
	}
}
