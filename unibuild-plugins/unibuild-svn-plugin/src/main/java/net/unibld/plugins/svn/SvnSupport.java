package net.unibld.plugins.svn;

import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.scm.BaseScmSupport;
import net.unibld.core.scm.IScmSupport;
import net.unibld.core.scm.RealmHelper;
import net.unibld.core.scm.StoredAuth;
import net.unibld.core.security.BuildCredential;
import net.unibld.core.security.CredentialStoreFactory;
import net.unibld.core.security.ICredentialStore;
import net.unibld.core.security.ICredentialStoreFactory;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.task.ExecutionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * The SVN implementation of the {@link IScmSupport} interface.<br>
 * The class is capable of both using built-in Subversion authentication store
 * via the {@link SvnAuthReader} class and UniBuild encrypted authentication store
 * via {@link CredentialStoreFactory}.
 * @author andor
 *
 */
@Component
public class SvnSupport extends BaseScmSupport<SvnCommand> implements IScmSupport<SvnCommand> {
	private static final Logger LOG=LoggerFactory.getLogger(SvnSupport.class);
	
	@Autowired
	private ICredentialStoreFactory credentialStoreFactory;
	private String builtInStorePath;
	@Override
	public <T extends SvnCommand> T create(Class<T> klazz) throws Exception {
		if (SvnCommand.class.isAssignableFrom(klazz)) {
			return klazz.newInstance();
		} else {
			throw new IllegalArgumentException("Class must be a subclass of SvnCommand: "+klazz.getName());
		}
	}

	@Override
	public ExecutionResult execute(SvnCommand cmd) {
		if (cmd==null) {
			throw new IllegalArgumentException("SVN command was null");
		}
		LOG.info("Executing SVN command: {}...",cmd.getClass().getName());

		if (cmd instanceof SvnExport) {
			ExecutionResult res=null;
			int idx=0;
			do {
				LOG.info("Exporting target {}: {}",idx,((SvnExport) cmd).getCurrentTarget());
				res=super.execute(cmd);
				idx++;
			} while (((SvnExport) cmd).nextTarget());
			
			if (res!=null) {
				return res;
			} else {
				return ExecutionResult.buildError("No execution was done");
			}
		} else {
			ExecutionResult res=super.execute(cmd);
			if (!res.isSuccess()) {
				if (cmd.getCredential()!=null&&cmd.getCredential().isAsked()&&
							cmd.getCredential().getAuthProviderType()!=null&&cmd.getCredential().getRealm()!=null
							&&cmd.getCredential().getUserName()!=null) {
					
					if (credentialStoreFactory!=null) {
						ICredentialStore store = credentialStoreFactory.getStore(PasswordStrategy.stored);
						if (store!=null) {
							LOG.info("Removing password just entered because of an error...");
							store.removeFromStore(cmd.getCredential().getAuthProviderType(),
									cmd.getCredential().getRealm(),
									cmd.getCredential().getUserName());
						}
					}
				}
			}
			return res;
		}
	}
	
	/**
	 * Retrieves a user password either from the built-in SVN auth store via
	 * {@link SvnAuthReader} or using the underlying {@link CredentialStoreFactory}
	 * instance.<br>
	 * The built-in SVN auth store is only used if the invoking {@link SvnTask}
	 * has its useBuiltinStore property set to true (by default, it is true)
	 * @param attributeContainer Build context attribute container
	 * @param task Invoking SVN task
	 * @return 
	 */
	public BuildCredential getPasswordForTask(IBuildContextAttributeContainer attributeContainer,SvnTask task) {
		if (task==null) {
			throw new IllegalArgumentException("SVN task was null");
		}
		if (task.getRepositoryUrl()==null||task.getUserName()==null) {
			return null;
		}
		
		
		String url = task.getRepositoryUrl();
		SvnAuthReader rdr=new SvnAuthReader();
		rdr.setExternalDir(builtInStorePath);
		
		StoredAuth a = rdr.readStoredAuth(url,task.getUserName());
		if (a!=null&& !StringUtils.isEmpty(a.getPassword()) && task.isUseBuiltinStore()) {
			if (!a.isEncrypted()) {
				LOG.info("Found simple authentication for URL {} in built-in SVN credential store",url);
			} else {
				LOG.info("Found crypted authentication for URL {} in built-in SVN credential store",url);
			}
			return BuildCredential.createExternalStore(task.getUserName(),
					(a.isEncrypted())?null:a.getPassword(),a.getRealm());
		}
		
		ICredentialStore store = credentialStoreFactory.getStore(task.getPasswordStrategy());
		LOG.info("Checking for SVN auth in credential store {} with strategy: {}...",store.getClass().getName(),task.getPasswordStrategy().name());
		
		return store.getPassword("svn", RealmHelper.createRealm(url), task.getUserName());
	}

	/**
	 * @return Credential store factory
	 */
	public ICredentialStoreFactory getCredentialStoreFactory() {
		return credentialStoreFactory;
	}


	
	/**
	 * @return Path of the built-in SVN auth store, set externally, typically from 
	 * a unit test.
	 */
	public String getBuiltInStorePath() {
		return builtInStorePath;
	}

	/**
	 * @param testBuiltInStorePath Path of the built-in SVN auth store, set externally, typically from 
	 * a unit test.
	 */
	public void setBuiltInStorePath(String testBuiltInStorePath) {
		this.builtInStorePath = testBuiltInStorePath;
	}

}
