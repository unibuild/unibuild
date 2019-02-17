package net.unibld.server.service.security;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.User;
import net.unibld.server.entities.security.UserProfile;

public interface SecurityService {

	UserProfile findUserProfile(String username);

	boolean isUserExisting(String userName);

	UserProfile saveUser(UserProfile p, Group group, String password) throws SpringSecurityException;
	public List<Authority> getAuthorities(String userName);

	List<UserProfile> getUserProfileByEmail(String email);
	
	UserProfile updateActivationMailSent(String userName);

	List<UserProfile> getUsersToNotifyAboutActivation();

	boolean isEmailExisting(String email);

	boolean hasRole(String userName, Group role);
	boolean hasEitherRole(String userName, Group... roles);

	Iterable<UserProfile> getAllUsers();

	Iterable<Authority> getAllAuthorities();

	List<UserProfile> getAllActiveUsersOfGroup(Group group);

	void banUser(UserProfile profile);

	void unbanUser(UserProfile profile);

	void updateLastLogin(String userId);

	User findUser(String username);
	AuthenticationManager getAuthenticationManager();
}
