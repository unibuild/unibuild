package net.unibld.core.util;

import org.springframework.beans.factory.support.AbstractBeanFactory;

public class PropertyPlaceHolderUtils {
	  public static String getProperty(AbstractBeanFactory beanFactory,String key) {
       
        String foundProp = null;
        try {
            foundProp = beanFactory.resolveEmbeddedValue("${" + key.trim() + "}");
        } catch (IllegalArgumentException ex) {
           // ok - property was not found
        }

        return foundProp;
    }

	public static int getPropertyAsInt(AbstractBeanFactory beanFactory,String key) {
		String val = getProperty(beanFactory, key);
		if (val==null) {
			throw new IllegalArgumentException("Invalid key: "+key);
		}
		return Integer.parseInt(val);
	}

	public static int getPropertyAsIntDefault(AbstractBeanFactory beanFactory,
			String key, int def) {
		String val = getProperty(beanFactory, key);
		if (val==null) {
			return def;
		}
		return Integer.parseInt(val);
	}
} 