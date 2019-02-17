package net.unibld.server.security;

import java.util.List;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.User;
import net.unibld.server.repositories.security.AuthorityRepository;
import net.unibld.server.repositories.security.UserRepository;
import net.unibld.server.service.security.SecurityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * A Spring Security user details manager implementation that uses JPA entities instead of 
 * direct JDBC access.
 * @author andor
 *
 */
public class JpaUserDetailsManager implements UserDetailsManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(JpaUserDetailsManager.class);
	   
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthorityRepository authorityRepository;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		return securityService.findUser(username);
	}

	@Override
	@Transactional
	public void createUser(UserDetails user) {
		//need to use user and groups 
		User u=(User) user;
		
		List<Authority> authorities = u.getAuthorityList();
		
		u=userRepository.save(u);
		if (authorities==null||authorities.size()==0) {
			throw new IllegalArgumentException("No authorities specified");
		}
		
		for (Authority a:authorities) {
			a.setUser(u);
			authorityRepository.save(a);
		}
		LOGGER.info("User created: {}",u.getUsername());
	}

	@Override
	public void updateUser(UserDetails user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteUser(String username) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean userExists(String username) {
		return securityService.findUserProfile(username)!=null;
	}

}
