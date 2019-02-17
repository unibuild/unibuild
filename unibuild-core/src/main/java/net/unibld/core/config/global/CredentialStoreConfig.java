package net.unibld.core.config.global;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.config.StoreConfig;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.util.PropertyPlaceHolderUtils;

/**
 * A configuration component for storing credentials for each {@link PasswordStrategy} available.
 * @author andor
 *
 */
@Component("credentialStoreConfig")
public class CredentialStoreConfig {
	

	private static final Logger LOGGER=LoggerFactory.getLogger(CredentialStoreConfig.class);
	
	@Autowired
	private AbstractBeanFactory beanFactory;
	
	private List<StoreConfig> storeConfig;
	
	/**
	 * Initializes the component on start up by loading {@link StoreConfig} objects for each {@link PasswordStrategy}
	 * available.
	 */
	@PostConstruct
	public void init() {
		for (PasswordStrategy strat:PasswordStrategy.values()) {
			loadStoreConfig(strat);
		}
	}

	private void loadStoreConfig(PasswordStrategy strat) {
		String name=strat.name();
		String klazzName=PropertyPlaceHolderUtils.getProperty(beanFactory, String.format("credentialstore.strategy.%s.class",name));
		if (klazzName==null) {
			LOGGER.info("No store config found: {}",strat.name());
			return;
		}
		
		StoreConfig cfg=new StoreConfig();
		cfg.setClassName(klazzName);
		cfg.setLocation(PropertyPlaceHolderUtils.getProperty(beanFactory, String.format("credentialstore.strategy.%s.location",name)));
		cfg.setStrategy(strat);
		if (storeConfig==null) {
			storeConfig=new ArrayList<>();
		}
		storeConfig.add(cfg);
		LOGGER.info("Credential store config loaded for strategy: {}",strat.name());
	}

	/**
	 * @return The underlying list of credential store configurations
	 */
	public List<StoreConfig> getStoreConfig() {
		return storeConfig;
	}

	/**
	 * @param storeConfig The underlying list of credential store configurations
	 */
	public void setStoreConfig(List<StoreConfig> storeConfig) {
		this.storeConfig = storeConfig;
	}

}
