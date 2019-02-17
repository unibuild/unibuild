package net.unibld.client.desktop.controller;

import org.springframework.util.StringUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import net.unibld.client.desktop.util.javafx.JavaFXController;

public class PasswordController extends JavaFXController {
	private PasswordManager passwordManager;
	
	@FXML
	private void initialize() {
		passwordField.setText("");
	}
	
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button specifyButton;
	
	@FXML
	private void specifyPassword() {
		String password=passwordField.getText();
		if (StringUtils.isEmpty(password)) {
			passwordManager.passwordSpecified(null);
		} else {
			passwordManager.passwordSpecified(password);
		}
		Stage stage = (Stage) specifyButton.getScene().getWindow();
		stage.close();
		
	}
	
	@FXML
	private void onEnter(ActionEvent ae) {
		String password=passwordField.getText();
		if (StringUtils.isEmpty(password)) {
			passwordManager.passwordSpecified(null);
		} else {
			passwordManager.passwordSpecified(password);
		}
		Stage stage = (Stage) specifyButton.getScene().getWindow();
		stage.close();
		ae.consume();
	}
	
	@FXML
	private void cancel() {
		passwordManager.passwordSpecified(null);
		Stage stage = (Stage) specifyButton.getScene().getWindow();
		stage.close();
		
	}

	public PasswordManager getPasswordManager() {
		return passwordManager;
	}

	public void setPasswordManager(PasswordManager passwordManager) {
		this.passwordManager = passwordManager;
	}

	
}
