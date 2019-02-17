package net.unibld.client.desktop.util.javafx;

import jtoast.notification.JToast;
import jtoast.notification.NotificationType;

public class ToastHelper {
	/**
	 * Shows an information message
	 * @param title Title of the message
	 * @param msg Body of the message
	 */
	public static void showInfoMessage(String title,String msg) {
		JToast tray = new JToast();
		tray.setTitle(title);
		tray.setMessage(msg);
		tray.setNotificationType(NotificationType.INFORMATION);
		tray.showAndWait();
	}

}
