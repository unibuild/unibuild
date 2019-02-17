package net.unibld.core.security;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.east301.keyring.BackendNotSupportedException;
import net.east301.keyring.Keyring;
import net.east301.keyring.PasswordRetrievalException;
import net.east301.keyring.PasswordSaveException;
import net.east301.keyring.util.LockException;
import net.unibld.core.config.StoreConfig;

/**
 * A credential store that uses the java-keyring library at <a>https://bitbucket.org/east301/java-keyring</a>
 * on Windows and OSX, using DPAPI and OSX Keychain respectively.<br>
 * Linux usage is not supported, see {@link LibSecretCredentialStore}.
 * @author andor
 *
 */
public class KeyringCredentialStore extends AbstractCredentialStore implements ICredentialStore {
	private static final Logger LOGGER = LoggerFactory.getLogger(KeyringCredentialStore.class);
	
	private Keyring keyring;
	
	private StoreConfig storeConfig;
	
	/**
	 * Constructor with store config
	 * @param cfg Store config
	 */
	public KeyringCredentialStore(StoreConfig cfg) {
		this.storeConfig=cfg;
		init();
	}
	
	/**
	 * Default constructor
	 */
	public KeyringCredentialStore() {
		this(null);
	}

	private void init() {


        // create an instance of Keyring by invoking Keyring.create method
        //
        // Keyring.create method finds appropriate keyring backend, and sets it up for you.
        // On Mac OS X environment, OS X Keychain is used, and On Windows environment,
        // DPAPI is used for encryption of passwords.
        // If no supported backend is found, BackendNotSupportedException is thrown.
        try {
            keyring = Keyring.create();
        } catch (BackendNotSupportedException ex) {
            LOGGER.error("Keyring could not be created",ex);
            throw new IllegalStateException("Keyring could not be created",ex);
        }

        // some backend directory handles a file to store password to disks.
        // in this case, we must set path to key store file by Keyring.setKeyStorePath
        // before using Keyring.getPassword and Keyring.getPassword.
        if (keyring.isKeyStorePathRequired()) {
        	File keyStoreFile = getKeyStoreFile();
    		keyStoreFile.getParentFile().mkdirs();
    		
            keyring.setKeyStorePath(keyStoreFile.getPath());
       
        }
	}
	private File getKeyStoreFile() {
		String path=FilenameUtils.concat(CredentialStoreFactory.getStoreDirPath(storeConfig),".keystore");
		return new File(path);
	}
	
	@Override
	public void clearAll() {
		File keyStoreFile = getKeyStoreFile();
		if (keyStoreFile.exists()&& keyStoreFile.isFile()) {
			keyStoreFile.delete();
		}
	}
	
	protected String getServiceName(CredentialKey key) {
		return String.format("%s|%s",key.getType(),key.getTarget());
	}

	@Override
	protected void putToStore(CredentialKey key, String passStr, String desc) {
		//
        // store password to key store
        //

        // Password can be stored to key store by using Keyring.setPassword method.
        // PasswordSaveException is thrown when some error happened while saving password.
        // LockException is thrown when keyring backend failed to lock key store file.
        try {
            keyring.setPassword(getServiceName(key), key.getIdentity(),  passStr);
        } catch (LockException ex) {
        	LOGGER.error("Could not lock keystore",ex);
       	 	throw new IllegalStateException("Could not lock keystore",ex);
        } catch (PasswordSaveException ex) {
        	LOGGER.error("Could not save password",ex);
       	 	throw new IllegalStateException("Could not save password",ex);
        }

	}

	@Override
	protected boolean storeContains(CredentialKey key) {
		return getFromStore(key)!=null;
	}

	@Override
	protected void removeFromStore(CredentialKey key) {
		//putToStore(key, null, null);
	}

	@Override
	protected String getFromStore(CredentialKey key) {
		//
        // Retrieve password from key store
        //

        // Password can be retrieved by using Keyring.getPassword method.
        // PasswordRetrievalException is thrown when some error happened while getting password.
        // LockException is thrown when keyring backend failed to lock key store file.
		 String name = getServiceName(key);
			
        try {
           return keyring.getPassword(name, key.getIdentity());
        } catch (LockException ex) {
        	LOGGER.error("Could not lock keystore",ex);
       	 	throw new IllegalStateException("Could not lock keystore",ex);
        } catch (PasswordRetrievalException ex) {
        	LOGGER.info("Could not retrieve password: {}",name);
       	 	return null;
        }
	}

}
