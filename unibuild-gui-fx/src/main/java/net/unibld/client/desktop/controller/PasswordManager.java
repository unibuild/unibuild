package net.unibld.client.desktop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import net.unibld.client.desktop.util.javafx.DialogHelper;
import net.unibld.client.desktop.util.javafx.DialogHelper.DialogWrapper;
import net.unibld.core.security.IPasswordReaderInterface;
import net.unibld.core.util.AsyncWaiter;

public class PasswordManager implements IPasswordReaderInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(PasswordManager.class);
	
	private MainController parent;

	private boolean passwordSpecified;
	private String password;

	protected DialogWrapper dialog;

	public PasswordManager(MainController parent) {
		this.parent=parent;
	}

	@Override
	public String readPassword(String credentialType, String target, String identity) {
		passwordSpecified=false;
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	try {
					LOGGER.info("Showing {} password dialog at {} for: {}...",credentialType,target,identity);
					PasswordManager.this.dialog = DialogHelper.createFxmlDialog("Password.fxml", getTitle(credentialType,target,identity));
					((PasswordController)dialog.getController()).setPasswordManager(PasswordManager.this);
					dialog.show();
					
				} catch (Exception ex) {
					LOGGER.error("Failed to open password dialog",ex);
					throw new IllegalStateException("Failed to open password dialog",ex);
				}
		 		
		    }
		});
		
		AsyncWaiter<Boolean> waiter=new AsyncWaiter<>(60000);
		waiter.waitForCondition(this::isPasswordSpecified);
		if (dialog!=null&&dialog.getStage().isShowing()) {
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			    	dialog.getStage().close();
			    }
			});
		}
		return password;
	}

	private String getTitle(String credentialType, String target, String identity) {
		return String.format("%s password for %s (%s)", credentialType,target,identity);
	}

	public void passwordSpecified(String password) {
		this.password=password;
		this.passwordSpecified=true;
	}

	public boolean isPasswordSpecified() {
		return passwordSpecified;
	}

}
