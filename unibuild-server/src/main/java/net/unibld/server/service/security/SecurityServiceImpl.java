package net.unibld.server.service.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.User;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.UserStatus;
import net.unibld.server.repositories.security.AuthorityRepository;
import net.unibld.server.repositories.security.UserProfileRepository;
import net.unibld.server.repositories.security.UserRepository;

@Service("securityService")
public class SecurityServiceImpl implements SecurityService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceImpl.class);
    
	@Autowired
	private UserDetailsManager userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthorityRepository authorityRepository;

	@Override
	public UserProfile findUserProfile(String username) {
		return userProfileRepository.findOne(username);
	}
	
	@Transactional
	public UserProfile saveUser(UserProfile p, Group group,String password) throws SpringSecurityException {
		if (p == null) {
			throw new SpringSecurityException("User profile was null");
		}
		if (p.getUserName() == null) {
			throw new SpringSecurityException("User name was null");
		}
		if (group == null) {
			throw new SpringSecurityException("User group was null");
		}
		if (p.getRegisterDate()==null&&userDetailsService.userExists(p.getUserName())) {
			throw new SpringSecurityException("User already exists: " + p.getUserName());
		}
		
		if (p.getRegisterDate()==null) {
			List<GrantedAuthority> authArr = AuthorityUtils.createAuthorityList(group.name());

			
			User user = new User();
			user.setUsername(p.getUserName());
			String encPasswd = passwordEncoder.encode(password);
			
			user.setPassword(encPasswd);
			user.setEnabled(true);
			user.addGrantedAuthorities(authArr);
			
			userDetailsService.createUser(user);
		} else {
		
			
			List<Authority> authorities = authorityRepository.findByUserName(p.getUserName());
			if (authorities.size()!=1) {
				throw new IllegalStateException("Invalid number of authorities: "+authorities.size());
			}
			Authority authority = authorities.get(0);
			authority.setAuthority(group.name());
			authorityRepository.save(authority);
		}
		
		p.setLastLogin(new Date());
		p=userProfileRepository.save(p);
		
		
		LOGGER.info("Successfully registered user: {}",p.getUserName());
		return p;
	}

	@Override
	public boolean isUserExisting(String userName) {
		return findUserProfile(userName)!=null;
	}

	public List<Authority> getAuthorities(String userName) {
		return authorityRepository.findByUserName(userName);
	}

	@Override
	public List<UserProfile> getUserProfileByEmail(String email) {
		return userProfileRepository.findByEmail(email);
	}

	@Transactional
	public UserProfile updateActivationMailSent(String userName) {
		LOGGER.info("Updating UserProfile after activation mail send: "+userName);
		UserProfile profileToSave = findUserProfile(userName);

		profileToSave.setActivationMailSent(true);
		return userProfileRepository.save(profileToSave);
	}
	
	
	/**
	 * @return A list of {@link UserProfile} records to notify about their activation
	 */
	public List<UserProfile> getUsersToNotifyAboutActivation() {
		return userProfileRepository.findNotActivatedAndNotified();
	}

	@Override
	public boolean isEmailExisting(String email) {
		return getUserProfileByEmail(email).size()>0;
	}

	@Override
	public boolean hasRole(String userName, Group role) {
		List<Authority> authorities = getAuthorities(userName);
		if (authorities.size()==0) {
			return false;
		}
		
		for (Authority a:authorities) {
			if (a.getAuthority().equals(role.name())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasEitherRole(String userName, Group... roles) {
		List<Authority> authorities = getAuthorities(userName);
		if (authorities.size()==0) {
			return false;
		}
		
		for (Authority a:authorities) {
			for (Group role:roles) {
				if (a.getAuthority().equals(role.name())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Iterable<UserProfile> getAllUsers() {
		return userProfileRepository.findAll();
	}

	@Override
	public Iterable<Authority> getAllAuthorities() {
		return authorityRepository.findAll();
	}

	@Override
	public List<UserProfile> getAllActiveUsersOfGroup(Group group) {
		if (group==null) {
			throw new IllegalArgumentException("Group was null");
		}
		Iterable<Authority> allAuthorities = getAllAuthorities();
		Map<String,Authority> authMap=new HashMap<String, Authority>();
		for (Authority a:allAuthorities) {
			if (a.getAuthority().equals(group.name())) {
				authMap.put(a.getUser().getUsername(), a);
			}
		}
		
		List<UserProfile> users=userProfileRepository.findByStatus(UserStatus.REGISTERED);
		List<UserProfile> ret=new ArrayList<UserProfile>();
		for (UserProfile u:users) {
			if (authMap.containsKey(u.getUserName())) {
				ret.add(u);
			}
		}
		return ret;
	}

	@Override
	@Transactional
	public void banUser(UserProfile profile) {
		profile.setStatus(UserStatus.BANNED);
		userProfileRepository.save(profile);
	}

	@Override
	@Transactional
	public void unbanUser(UserProfile profile) {
		profile.setStatus(UserStatus.REGISTERED);
		userProfileRepository.save(profile);
	}

	@Override
	@Transactional
	public void updateLastLogin(String userId) {
		UserProfile p = findUserProfile(userId);
		if (p==null) {
			throw new IllegalArgumentException("Invalid user id: "+userId);
		}
		p.setLastLogin(new Date());
		userProfileRepository.save(p);
	}

	@Override
	public User findUser(String username) {
		return userRepository.findOne(username);
	}

	@Override
	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}


}
