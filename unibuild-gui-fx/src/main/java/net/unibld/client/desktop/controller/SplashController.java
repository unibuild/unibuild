package net.unibld.client.desktop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.unibld.client.desktop.ApplicationConstants;
import net.unibld.client.desktop.ApplicationHolder;
import net.unibld.client.desktop.SpringBeanFactory;
import net.unibld.client.desktop.util.javafx.HostServicesControllerFactory;
import net.unibld.client.desktop.util.javafx.JavaFXController;

/**
 * JavaFX controller for the splash screen. It is also responsible for checking for newer versions.
 * @author andor
 *
 */
public class SplashController extends JavaFXController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SplashController.class);
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private Label splashLabel;
	
	

	/**
	 * Constructor with JavaFX host services
	 * @param hostServices JavaFX host services
	 */
	public SplashController(HostServices hostServices) {
		this.setHostServices(hostServices);
	}
	
	/**
	 * Initializes the controller
	 */
	@FXML
	private void initialize() {
		splashLabel.setText("Starting UniBuild desktop client...");
		// loads the items at another thread, asynchronously
		Task<Void> listLoader = new Task<Void>() {
			
			@Override
			protected Void call() {
				LOGGER.info("Waiting for Spring context...");
				
				while (!SpringBeanFactory.isContextInitialized()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						LOGGER.error("Failed to sleep while waiting for Spring context",e);
					}
					
				}
				return null;
			}
		};

		listLoader.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
					startFirstScreen();
				
			}
		});

		listLoader.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				LOGGER.error("Initializing Spring context failed", listLoader.getException());
				ApplicationHolder.getApplication().exitApplicationWithError();
			}
		});
		
		
		
		Thread loadingThread = new Thread(listLoader, "ApplicationInitThread");
		loadingThread.setDaemon(true);
		loadingThread.start();
	}

	
	
	private String getStartFxml() {
		return "/fxml/Main.fxml";
	}

	/**
	 * Creates a main stage and sets the starting scene (using getStartFxml).
	 */
	protected void startFirstScreen() {
		
		try {
			Stage stage = (Stage) progressIndicator.getScene().getWindow();
			// load up OTHER FXML document
			String fxml = getStartFxml();
			FXMLLoader loader = new FXMLLoader(SplashController.class.getResource(fxml));
			loader.setControllerFactory(new HostServicesControllerFactory(getHostServices()));
			Parent root = loader.load();

			// create a new scene with root and set the stage
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(ApplicationConstants.XML_EDITOR_CSS_PATH).toExternalForm());



			// create your main stage.
			Stage mainStage = new Stage();
			mainStage.setScene(scene);
			mainStage.initStyle(StageStyle.DECORATED);
			mainStage.resizableProperty().setValue(Boolean.FALSE);
			
			mainStage.setTitle(ApplicationConstants.APP_TITLE);
			
			//mainStage.getIcons().add(
			//		   new Image(
			//		      getClass().getResourceAsStream( ApplicationConstants.APP_ICON_PATH ))); 
			
			if (fxml.equals("/fxml/Main.fxml")) {
				mainStage.setMaximized(true);
				mainStage.resizableProperty().setValue(Boolean.TRUE);
				
			}
			
			stage.hide();
			mainStage.show();
		} catch (Exception ex) {
			LOGGER.error("Failed to navigate to starting page", ex);
			ApplicationHolder.getApplication().exitApplicationWithError();
		}
	}
}
