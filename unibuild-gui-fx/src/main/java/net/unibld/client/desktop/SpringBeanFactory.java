package net.unibld.client.desktop;

import org.springframework.context.ApplicationContext;

/**
 * Factory class for Spring beans that holds a static reference to the singleton Spring
 * {@link ApplicationContext}
 * @author andor
 *
 */
public class SpringBeanFactory {
	private static ApplicationContext springContext;

	/**
	 * Sets the Spring singleton ApplicationContext on init
	 * @param ctx Spring singleton ApplicationContext
	 */
	public static void contextInitialized(ApplicationContext ctx) {
		springContext=ctx;
		
	}
	
	/**
	 * @return Returns true if the Spring singleton ApplicationContext has been set
	 */
	public static boolean isContextInitialized() {
		return springContext!=null;
	}
	
	public static <T> T getBean(Class<T> klazz) {
		return springContext.getBean(klazz);
	}

	/**
	 * @return Spring singleton ApplicationContext
	 */
	public static ApplicationContext getSpringContext() {
		return springContext;
	}

	
}
