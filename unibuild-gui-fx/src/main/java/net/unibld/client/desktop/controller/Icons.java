package net.unibld.client.desktop.controller;

import javafx.scene.image.Image;

public class Icons {
	public static Image getIcon(String name) {
		return new Image(Icons.class.getResourceAsStream("/images/icons/"+name+".png"));
	}
	
	public static Image getWaitingIcon() {
		return getIcon("control_play");
	}
	public static Image getRunningIcon() {
		return getIcon("hourglass");
	}
	
	public static Image getDoneIcon() {
		return getIcon("accept");
	}
	
	public static Image getFailedIcon() {
		return getIcon("cancel");
	}
	
}
