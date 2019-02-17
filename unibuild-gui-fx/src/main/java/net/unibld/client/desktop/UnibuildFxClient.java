package net.unibld.client.desktop;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.unibld.client.desktop.util.javafx.DialogHelper;
import net.unibld.client.desktop.util.javafx.HostServicesControllerFactory;
import net.unibld.core.util.PlatformHelper;

/**
 * Kinepict desktop CE edition JavaFX application class.
 * @author andor
 *
 */
@SpringBootApplication
@ImportResource("classpath:applicationContextDesktop.xml")
public class UnibuildFxClient extends AbstractConfigurableJavaFxApplicationSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(UnibuildFxClient.class);
	

	/**
	 * Main method
	 * @param args Program arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	private Stage mainStage;
	
	
	@Override
	public void init() throws Exception {
		super.init();
		
		ApplicationHolder.setApplication(this);
		
		
		
		LOGGER.info("Application starting...");
		LOGGER.info("Enviroment: {}", PlatformHelper.getEnvironmentTrace());
		

		LOGGER.info("Launching JavaFX application...");
		
	}
	
	

	@Override
	public void start(Stage primaryStage) {
		this.mainStage=primaryStage;
		//mainStage.getIcons().add(
		//		   new Image(
		//		      getClass().getResourceAsStream( ApplicationConstants.APP_ICON_PATH ))); 
		try {
			Thread.setDefaultUncaughtExceptionHandler(
					(t, e) -> Platform.runLater(() -> DialogHelper.showErrorDialog(t, e)));
			Thread.currentThread().setUncaughtExceptionHandler(DialogHelper::showErrorDialog);
			
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setOnCloseRequest(confirmCloseEventHandler);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
			loader.setControllerFactory(new HostServicesControllerFactory(getHostServices()));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			LOGGER.error("Application crashed during startup: {}...", e);
		}
	
	}

	
	public void exitApplication() {
		
		SpringApplication.exit(applicationContext);
		Platform.exit();
	}
	public void exitApplicationWithError() {
		SpringApplication.exit(applicationContext,new ExitCodeGenerator() {
			@Override
			public int getExitCode() {
				return 1;
			}
		});
		Platform.exit();
	}
	
	private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
		/*boolean generating = SpringBeanFactory.getBean(WorkspaceContext.class).isGenerating();
		
		Workspace ws = SpringBeanFactory.getBean(WorkspaceContext.class).getWorkspace();
		if (!generating && (ws==null||ws.getInputImage()==null||ws.getOutputImages()==null||!ws.hasChanges())) {
			exitApplication();
			return;
		}
		
		String text=generating?
        		"You are in the process of generating images. Are you sure you want to stop and exit?":
        		"Are you sure you want to exit?";
        Optional<ButtonType> closeResponse = DialogHelper.showConfirmDialog(mainStage,"Confirm Exit",text,"EXIT");
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        } else {
        	exitApplication();
        }*/
    };

	
	

	/**
	 * @return Close confirm event handler
	 */
	public EventHandler<WindowEvent> getConfirmCloseEventHandler() {
		return confirmCloseEventHandler;
	}

	/**
	 * @return Main stage
	 */
	public Stage getMainStage() {
		return mainStage;
	}
}
