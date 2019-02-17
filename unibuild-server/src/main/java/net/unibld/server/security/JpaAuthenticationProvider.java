package net.unibld.server.security;

import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.UserStatus;
import net.unibld.server.service.security.SecurityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * A Spring Security authentication provider that uses JPA entities instead of direct JDBC
 * access.
 * @author andor
 *
 */
public class JpaAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(JpaAuthenticationProvider.class);
    
	@Autowired
	private UserDetailsManager userDetailsService;
	
	@Autowired
	private SecurityService securityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	
	@Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        
        if (authentication.getCredentials() == null) {
            LOGGER.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            LOGGER.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        
        UserProfile p = securityManager.findUserProfile(userDetails.getUsername());
		if (p==null||!p.isActivated()||p.getStatus()==UserStatus.DELETED||p.getStatus()==UserStatus.BANNED) {
			LOGGER.info("Authentication failed: no UserProfile found or wrong status: "+userDetails.getUsername());

			 throw new BadCredentialsException(messages.getMessage(
	                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
	    }
    }


	@Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
    throws AuthenticationException {
		UserDetails loadedUser;
		
		try {
		    loadedUser = this.userDetailsService.loadUserByUsername(username);
		} catch (UsernameNotFoundException notFound) {
		    throw notFound;
		} catch (Exception repositoryProblem) {
		    throw new AuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
		}
		
		if (loadedUser == null) {
		    throw new AuthenticationServiceException(
		            "UserDetailsService returned null, which is an interface contract violation");
		}
		return loadedUser;
	}


	/**
	 * @return Spring Security user details manager
	 */
	public UserDetailsManager getUserDetailsService() {
		return userDetailsService;
	}


	/**
	 * @param userDetailsService Spring Security user details manager
	 */
	public void setUserDetailsService(UserDetailsManager userDetailsService) {
		this.userDetailsService = userDetailsService;
	}


	

	
}
