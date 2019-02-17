package net.unibld.core.security;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.revelc.code.gnome.keyring.GnomeKeyring;
import net.revelc.code.gnome.keyring.GnomeKeyringException;
import net.revelc.code.gnome.keyring.GnomeKeyringItem;
import net.unibld.core.config.StoreConfig;
import net.unibld.core.util.PlatformHelper;

public class GnomeKeyringCredentialStore extends AbstractCredentialStore implements ICredentialStore {
	private static final Logger LOGGER = LoggerFactory.getLogger(GnomeKeyringCredentialStore.class);
	
	
	private StoreConfig storeConfig;


	private GnomeKeyring keyring;


	private Map<CredentialKey, String> cache;
	
	public GnomeKeyringCredentialStore(StoreConfig cfg) {
		this.storeConfig=cfg;
		init();
	}

	private void init() {
		if (!PlatformHelper.isLinux()) {
			throw new IllegalStateException("GnomeKeyringCredentialStore can only be used on Linux");
		}
		
		keyring=new GnomeKeyring("unibld");
	}

	@Override
	public void clearAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void putToStore(CredentialKey key, String passStr, String desc) {
		try {
			String keyringName = keyring.getDefaultKeyring();
			keyring.setNetworkPassword(keyringName, key.getIdentity(), key.getTarget(), null, null, null, null, 0, passStr);
			cache=null;
		} catch (GnomeKeyringException ex) {
			LOGGER.error("Failed to put to Gnome keyring",ex);
			throw new IllegalStateException("Failed to put to Gnome keyring",ex);
		}
	}

	@Override
	protected boolean storeContains(CredentialKey key) {
		loadData();
		return cache.containsKey(key);
	}

	@Override
	protected void removeFromStore(CredentialKey key) {
		
	}

	private void loadData() {
		if (cache!=null) {
			LOGGER.debug("Data already loaded, skipping...");
			return;
		}
        try {

	        String keyringName = keyring.getDefaultKeyring();
	
	        cache = new HashMap<>(); 
	
	        for(Integer i: keyring.getIds(keyringName)) {
	            GnomeKeyringItem item = keyring.getItem(keyringName, i, true);
	            CredentialKey key=toCredentialKey(item);
	            cache.put(key, item.getSecret());
	        }
        } catch (GnomeKeyringException ex) {
        	LOGGER.error("Failed to load keyring data",ex);
        	
        }
    }
	
	private CredentialKey toCredentialKey(GnomeKeyringItem item) {
		
		return null;
	}

	@Override
	protected String getFromStore(CredentialKey key) {
		loadData();
		return cache.get(key);
		
	}
}
