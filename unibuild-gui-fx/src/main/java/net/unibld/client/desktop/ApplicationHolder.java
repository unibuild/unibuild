package net.unibld.client.desktop;

/**
 * Holder class for the singleton application instance
 * @author andor
 *
 */
public class ApplicationHolder {
	private static UnibuildFxClient application;
	/**
	 * @return Singleton application instance
	 */
	public static UnibuildFxClient getApplication() {
		return application;
	}
	/**
	 * @param application Singleton application instance
	 */
	public static void setApplication(UnibuildFxClient application) {
		ApplicationHolder.application = application;
	}
}
