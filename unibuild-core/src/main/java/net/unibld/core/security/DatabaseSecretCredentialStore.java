package net.unibld.core.security;

import java.io.IOException;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.service.SecretStoreService;
import net.unibld.core.util.Crypto;

public class DatabaseSecretCredentialStore extends AbstractCredentialStore implements ISecretStoreConsumer {
	private static final Logger LOG=LoggerFactory.getLogger(DatabaseSecretCredentialStore.class);
	
	private static final String PASSWORD_STORE_PASS = "TupvhwkV4y5u";
	private static final int PASSWORD_KEY_LENGTH = 128;
	
	private SecretStoreService secretStoreService;
	
	@Override
	public void clearAll() {
		secretStoreService.deleteAll();
	}

	@Override
	public void setSecretStoreService(SecretStoreService service) {
		this.secretStoreService=service;
	}

	@Override
	protected void putToStore(CredentialKey key, String passStr, String desc) {
		
		String str = getKeyAsString(key);
		LOG.info("Storing value in secret store with key: {}",str);
		secretStoreService.putValue(str, encrypt(passStr));
	}
	
	private String encrypt(String passStr) {
		SecretKey key = Crypto.deriveKeyPad(PASSWORD_STORE_PASS,PASSWORD_KEY_LENGTH);
		return Crypto.encrypt(passStr,key,null);
	}

	protected String getKeyAsString(CredentialKey key) {
		return String.format("%s|%s|%s",key.getType(),key.getTarget(),key.getIdentity());
	}


	@Override
	protected boolean storeContains(CredentialKey key) {
		
		String str = getKeyAsString(key);
		LOG.info("Checking for secret store key: {}...",str);
		return secretStoreService.isKeyExisting(str);
	}

	@Override
	protected void removeFromStore(CredentialKey key) {
		this.secretStoreService.deleteKey(getKeyAsString(key));
	}

	@Override
	protected String getFromStore(CredentialKey key) {
		return decrypt(this.secretStoreService.getValue(getKeyAsString(key)));
	}

	private String decrypt(String str) {
		SecretKey key = Crypto.deriveKeyPad(PASSWORD_STORE_PASS,PASSWORD_KEY_LENGTH);
		try {
			return Crypto.decryptNoSalt(str,key);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to decrypt value",e);
		}
	}

}
