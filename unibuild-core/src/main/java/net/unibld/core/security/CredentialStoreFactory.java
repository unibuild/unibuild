package net.unibld.core.security;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.unibld.core.config.StoreConfig;
import net.unibld.core.config.global.CredentialStoreConfig;
import net.unibld.core.config.global.GlobalConfig;
import net.unibld.core.service.SecretStoreService;
import net.unibld.core.util.PlatformHelper;

/**
 * A factory class that creates {@link ICredentialStore} implementations depending
 * on the {@link PasswordStrategy} required
 * @author andor
 *
 */
public class CredentialStoreFactory implements ICredentialStoreFactory {
	private static final Logger LOG=LoggerFactory.getLogger(CredentialStoreFactory.class);
	
	@Autowired
	private CredentialStoreConfig config;
	
	@Autowired
	private SecretStoreService secretStoreService;
	
	private Map<PasswordStrategy,ICredentialStore> stores=new Hashtable<>(); 
	/* (non-Javadoc)
	 * @see net.unibld.core.security.ICredentialStoreFactory#getStore(net.unibld.core.task.PasswordStrategy)
	 */
	@Override
	public ICredentialStore getStore(PasswordStrategy strategy) {
		if (stores.containsKey(strategy))  {
			return stores.get(strategy);
		} else {
			ICredentialStore ips=createCredentialStore(strategy);
			stores.put(strategy, ips);
			return ips;
		}
				
			
		
	}
	private ICredentialStore createCredentialStore(PasswordStrategy strategy) {
		if (config!=null&&config.getStoreConfig()!=null) {
			StoreConfig sc=null;
			for (StoreConfig s:config.getStoreConfig()) {
				if (s.getStrategy()==strategy) {
					sc=s;
					break;
				}
			}
			
			if (sc!=null&&sc.getClassName()!=null) {
				LOG.info("Creating configured store for strategy {}: {}...",sc.getStrategy(),sc.getClassName());
				return createStore(sc);
			}
		}
		return createDefaultCredentialStore(strategy);
	}
	private ICredentialStore createStore(StoreConfig sc) {
		if (sc==null) {
			throw new IllegalArgumentException("Store config was null");
		}
		if (sc.getClassName()==null) {
			throw new IllegalArgumentException("Store config class was null");
		}
		
		try {
			Class<? extends ICredentialStore> klazz = (Class<? extends ICredentialStore>) Class.forName(sc.getClassName());
			Constructor<? extends ICredentialStore> c =null;
			try {
				c = klazz.getConstructor(StoreConfig.class);
			} catch (Exception e2) {
				LOG.debug("No constructor found with StoreConfig: {}",klazz.getName());
			}
			
			ICredentialStore store =null;
			if (c==null) {
				store = klazz.newInstance();
				LOG.info("Created store with default constructor: {}",klazz.getName());
			} else {
				store = c.newInstance(sc);
				LOG.info("Created store with StoreConfig: {}",klazz.getName());
			}
			
			if (ISecretStoreConsumer.class.isAssignableFrom(store.getClass())) {
				((ISecretStoreConsumer)store).setSecretStoreService(secretStoreService);
			}
			return store;
		} catch (Exception ex) {
			LOG.error("Invalid credential store class: "+sc.getClassName(),ex);
			throw new IllegalStateException("Invalid credential store class: "+sc.getClassName(),ex);
		}
		
	}
	private ICredentialStore createDefaultCredentialStore(
			PasswordStrategy strategy) {
		if (strategy==null) {
			throw new IllegalArgumentException("Password strategy was null");
		}
		LOG.info("Creating default store for strategy: {}...",strategy);
		ICredentialStore store=null;
		switch (strategy) {
			case ask:
				store = new InMemoryCredentialStore();
				break;
			case stored:
				store= PlatformHelper.isLinux()?new DatabaseSecretCredentialStore():new KeyringCredentialStore();
				break;
			default:
				throw new IllegalArgumentException(String.format("Invalid password strategy: %s",strategy));
		}

		if (ISecretStoreConsumer.class.isAssignableFrom(store.getClass())) {
			((ISecretStoreConsumer)store).setSecretStoreService(secretStoreService);
		}
		return store;
	}
	

	public static String getStoreDirPath(StoreConfig storeConfig) {
		if (storeConfig!=null&&storeConfig.getLocation()!=null) {
			return storeConfig.getLocation();
		}
		return FilenameUtils.concat(PlatformHelper.getUserHome(), GlobalConfig.getSoftwareName());
	}
}
