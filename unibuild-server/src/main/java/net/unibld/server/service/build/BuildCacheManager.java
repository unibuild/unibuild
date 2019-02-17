package net.unibld.server.service.build;

import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

/**
 * A cache for current build states on the server side, using EHCache. It is used on the website to access current build state in 
 * a rapid way. 
 * @author andor
 *
 */
@Component
public class BuildCacheManager {
private static final Logger LOGGER = LoggerFactory.getLogger(BuildCacheManager.class);
	
	@Autowired
	private EhCacheCacheManager cacheManager;
	
	/**
	 * Flushes the build cache
	 */
	public void flushAll() {
		LOGGER.info("Flushing EhCache 'buildCache' completely...");
		cacheManager.getCacheManager().getCache("buildCache").removeAll();
	}

	public BuildState getBuildState(String id) {
		Element element = cacheManager.getCacheManager().getCache("buildCache").get("state."+id);
		if (element!=null) {
			return (BuildState) element.getObjectValue();			
		}
		return null;
	}

	public void putBuildState(String buildId, BuildState state) {
		cacheManager.getCacheManager().getCache("buildCache").put(
				new Element("state."+buildId, state));

	}
}
