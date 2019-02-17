package net.unibld.plugins.svn;

import net.unibld.core.build.BuildException;
import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.security.BuildCredential;
import net.unibld.core.security.CredentialType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SvnCheckout extends SvnCommand {
	private static final Logger LOG=LoggerFactory.getLogger(SvnCheckout.class);
	private String repositoryPath;
	
	@Override
	public String getArguments() {
		if (repositoryUrl!=null) {
			BuildCredential cred=getCredential();
			if (cred==null||cred.getType()==CredentialType.NONE) {
				throw new BuildException("No credentials provided");
			}
			if (!hasEnoughInformation(cred)) {
				throw new BuildException("Not enough information provided in credentials");
			}
			if (isPassingUserName(cred)&&isPassingPassword(cred)) {
				return String.format("%s %s --username %s --password %s",getCommand(),getRepositoryUrl(),getUserName(),cred.getPassword());
			} else if (isPassingUserName(cred)) {
				return String.format("%s %s --username %s",getCommand(),getRepositoryUrl(),getUserName());
			} else {
				return String.format("%s %s",getCommand(),getRepositoryUrl());
			}
		} else {
			return String.format("%s file://%s",getCommand(),getRepositoryPath());
		}
	}
	private boolean hasEnoughInformation(BuildCredential cred) {
		if (cred==null||cred.getType()==null||cred.getType()==CredentialType.NONE){
			return false;
		}
		
		if (cred.getType()==CredentialType.BUILD_PROVIDED) {
			return cred.getUserName()!=null&&cred.getPassword()!=null;
		}
		if (cred.getType()==CredentialType.EXTERNALLY_PROVIDED) {
			return true;
		}
		return false;
	}
	private boolean isPassingPassword(BuildCredential cred) {
		if (cred==null) {
			return false;
		}
		if (cred.getType()==CredentialType.EXTERNALLY_PROVIDED) {
			//never pass a password if SVN auth was found
			return false;
		}
		if (cred.getType()==CredentialType.BUILD_PROVIDED) {
			//always pass password if it exists, if UniBuild auth store is used
			return cred.getPassword()!=null;
		}
		return false;
	}
	private boolean isPassingUserName(BuildCredential cred) {
		if (cred==null) {
			return false;
		}
		if (cred.getType()==CredentialType.EXTERNALLY_PROVIDED) {
			//only passing user name if SVN auth was found if no username
			//could be detected
			return cred.getUserName()==null;
		}
		if (cred.getType()==CredentialType.BUILD_PROVIDED) {
			//always pass username if UniBuild auth store is used
			return true;
		}
		return false;
	}

	@Override
	public String getCommand() {
		return "checkout";
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	@Override
	public void prepare(IBuildContextAttributeContainer container,SvnTask task) {
		LOG.debug("Creating dir {}...",task.getCheckoutDir());
		this.checkoutDir=task.getCheckoutDir();
		this.repositoryPath=task.getRepositoryPath();
		this.repositoryUrl=task.getRepositoryUrl();
		this.userName=task.getUserName();
		this.useBuiltinStore=task.isUseBuiltinStore();
		
		
		super.useCheckoutDirAsWorkingDir(container,task);
	}

	
}
