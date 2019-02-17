package net.unibld.server.service.security.invitation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.UserStatus;
import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.entities.security.invitation.InvitationStatus;
import net.unibld.server.repositories.security.InvitationRepository;
import net.unibld.server.repositories.security.UserProfileRepository;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.service.security.SpringSecurityException;
import net.unibld.server.service.security.mail.SecurityMailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("invitationService")
public class InvitationServiceImpl implements InvitationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InvitationServiceImpl.class);
	   
	
	
	@Autowired
	private SecurityService securityService;
	@Autowired
	private SecurityMailService securityMailService;
	@Autowired
	private InvitationRepository invitationRepository;
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Value("${register.default.language:'en'}")
	private String registerDefaultLanguage;
	
	
	@Transactional
	public Invitation cancelInvitation(long invitationID, String userId) {
		Invitation i = findInvitation(invitationID);
		if (i==null) {
			throw new IllegalArgumentException("Invitation not found: "+invitationID);
		}
		
		if (!i.isCancellable()) {
			throw new IllegalArgumentException("Invitation not cancellable: "+invitationID);
		}
		
		i.setStatus(InvitationStatus.CANCELLED);
		
		i.setModifier(userId);
		return invitationRepository.save(i);
	
	}

	
	@Transactional
	public Invitation inviteUser(String invitedEmail, UserProfile invitor,Group group) throws SpringSecurityException  {
		List<Invitation> ret = inviteUsers(Arrays.asList(invitedEmail), invitor,group);
		if (ret==null||ret.size()==0) {
			return null;
		}
		
		return ret.get(0);
	}
	
	@Transactional
	public List<Invitation> inviteUsers(List<String> invitedEmails,
			UserProfile invitor,Group invitedGroup) throws SpringSecurityException  {
		List<Invitation> ret=new ArrayList<Invitation>();
		for (String email:invitedEmails) {
			Invitation inv = newInvitation(email, invitedGroup, invitor);
			securityMailService.sendInvitationMail(inv);
			ret.add(inv);
		}
		return ret;
	}
	@Transactional
	public Invitation newInvitation(String email, Group invitedGroup,
			UserProfile invitor) {
		Invitation inv = createInvitation(email, invitedGroup, invitor);
		inv.setCreator(invitor.getUserName());
		inv.setModifier(invitor.getUserName());
		return invitationRepository.save(inv);

	}
	

	private Invitation createInvitation(String email, Group invitedGroup,
			UserProfile invitor) {
		Invitation inv=new Invitation();
		inv.setInvitor(invitor);
		inv.setEmail(email);
		inv.setStatus(InvitationStatus.ACTIVE);
		
		String uid = UUID.randomUUID().toString();
		inv.setAuthCode(uid);
		inv.setInvitedGroup(invitedGroup);
		return inv;
	}
	public List<Invitation> getInvitationByEmail(String email) {
		return invitationRepository.findByEmail(email);
	
	}
	
	/**
	 * @return Active player invitations in the database, sent by captains
	 */ 
	public List<Invitation> getInvitationsActive() {
		return invitationRepository.findByStatus(InvitationStatus.ACTIVE);
	
	}
	public Invitation getInvitationSentByAuthCode(String authCode) {
		return invitationRepository.findByAuthCodeAndStatus(authCode,InvitationStatus.SENT);
	}

	@Transactional
	public Invitation resendInvitation(long invitationID,String userId) throws SpringSecurityException {
		Invitation i = findInvitation(invitationID);
		if (i==null) {
			throw new IllegalArgumentException("Invitation not found: "+invitationID);
		}
		
		
		Invitation i2=new Invitation();
		i2.setEmail(i.getEmail());
		i2.setStatus(InvitationStatus.ACTIVE);
		i2.setInvitor(i.getInvitor());
		i2.setInvitedGroup(i.getInvitedGroup());
		String uid = UUID.randomUUID().toString();
		i2.setAuthCode(uid);
		
		i2.setCreator(userId);
		i2.setModifier(userId);
		
		i2=invitationRepository.save(i2);
		
		
		i.setStatus(InvitationStatus.CANCELLED);
		i.setModifier(userId);
		i=invitationRepository.save(i);
		
		securityMailService.sendInvitationMail(i2);
		return i2;
		
	}
	
	@Transactional
	public Invitation setInvitationStatus(long invId,InvitationStatus status) {
		Invitation inv = findInvitation(invId);
		if (inv==null) {
			throw new IllegalArgumentException("Invalid invitation id: "+invId);
		}
		inv.setStatus(status);
		inv=invitationRepository.save(inv);
		
		return inv;
	}
	
	
	
	public List<Invitation> getInvitationsAll() {
		return invitationRepository.findAllByOrderByCreateDate();
	}

	
	
	public Invitation findInvitation(long id) {
		return invitationRepository.findOne(id);
		
	}
	@Transactional
	public UserProfile registerInvitedUser(UserProfile profile,Invitation inv,String password) throws SpringSecurityException {
		UserProfile registeredProfile =  securityService.saveUser(profile, inv.getInvitedGroup()!=null?inv.getInvitedGroup():Group.ROLE_USER, password);
		
		if (registeredProfile==null) {
			throw new SpringSecurityException("Registered profile was null");
		}
		
			
			
		registeredProfile.setActivated(true);
		
		registeredProfile.setActivationMailSent(true);
		registeredProfile.setStatus(UserStatus.REGISTERED);
		
	
		registeredProfile=userProfileRepository.save(registeredProfile);
		
		
		
		
		setInvitationStatus(inv.getId(),InvitationStatus.USED);
	
		return registeredProfile;
	
	}

	public List<UserProfile> searchUsersForInvite(String search) {
		return userProfileRepository.searchUsers(search.toLowerCase().trim()+"%");
		
	}

	@Transactional
	public void deleteUserInvitationData(UserProfile profile) {
		if (profile==null) {
			throw new IllegalArgumentException("Profile was null");
		}
		if (profile.getUserName()==null) {
			throw new IllegalArgumentException("User id was null");
		}
		
		long rows=invitationRepository.deleteByInvitor(profile);
		
		LOGGER.info(String.format("Deleted %d invitations of %s: ",rows,profile.getUserName()));
		
		
		LOGGER.info("Deleted user invitation data for: "+profile.getUserName());
	
	}


	@Override
	@Transactional
	public Invitation updateInvitationQueued(Invitation inv) {
		inv.setStatus(InvitationStatus.QUEUED_TO_SEND);
		return invitationRepository.save(inv);
	}

	@Override
	@Transactional
	public Invitation updateInvitationSent(Invitation inv) {
		inv.setStatus(InvitationStatus.SENT);
		inv.setSendDate(new Date());
		return invitationRepository.save(inv);
	}


	public String getRegisterDefaultLanguage() {
		return registerDefaultLanguage;
	}


	@Override
	@Transactional
	public Invitation updateInvitationError(Invitation inv) {
		inv.setStatus(InvitationStatus.FAILED);
		return invitationRepository.save(inv);
	}
}
