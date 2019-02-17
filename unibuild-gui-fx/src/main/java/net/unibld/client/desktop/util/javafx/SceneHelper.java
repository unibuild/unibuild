package net.unibld.client.desktop.util.javafx;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Helper class related to Java FX scenes.
 * @author andor
 *
 */
public class SceneHelper {
	private static final Logger LOGGER=LoggerFactory.getLogger(SceneHelper.class);
	
	public static void switchScene(String newScene, Node fromNode, Class<?> klassz, JavaFXController controller,String title) {
		switchScene(newScene, fromNode, klassz, controller,title,false,false,null);
	}
	public static void switchScene(String newScene, Node fromNode, Class<?> klassz, JavaFXController controller, 
			String title,boolean resizable,boolean maximized,EventHandler<WindowEvent> closeHandler) {
		LOGGER.info("Changing to " + newScene + "...");
	
		Stage stage = (Stage) fromNode.getScene().getWindow();
		FXMLLoader loader = new FXMLLoader(klassz.getClass().getResource("/" + newScene));
		loader.setControllerFactory(new HostServicesControllerFactory(controller.getHostServices()));
		Parent root;
		Scene scene;
		try {
			root = loader.load();
			// create a new scene with root and set the stage
			scene = new Scene(root);
			//scene.getStylesheets().add(SceneHelper.class.getResource(ApplicationConstants.CSS_PATH).toExternalForm());

			stage.setScene(scene);
			stage.resizableProperty().setValue(resizable);
			if (closeHandler!=null) {
				stage.setOnCloseRequest(closeHandler);
			}
			
			if (maximized) {
				stage.setMaximized(true);
			} else {
				stage.centerOnScreen();
			}
			if (title!=null) {
				stage.setTitle(title);
			}
			stage.show();
		} catch (IOException e) {
			DialogHelper.showErrorDialog(e);
		}
	}
	
	public static void switchToMainScene(String newScene, Node fromNode, Class<?> klassz, JavaFXController controller, 
			String title,boolean resizable,boolean maximized,EventHandler<WindowEvent> closeHandler) {
		LOGGER.info("Changing to " + newScene + "...");
	
		Stage stage = (Stage) fromNode.getScene().getWindow();
		FXMLLoader loader = new FXMLLoader(klassz.getClass().getResource("/" + newScene));
		loader.setControllerFactory(new HostServicesControllerFactory(controller.getHostServices()));
		Parent root;
		Scene scene;
		try {
			root = loader.load();
			// create a new scene with root and set the stage
			scene = new Scene(root);
			//scene.getStylesheets().add(SceneHelper.class.getResource(ApplicationConstants.CSS_PATH).toExternalForm());
			

			stage.setScene(scene);
			
			
			stage.resizableProperty().setValue(resizable);
			if (closeHandler!=null) {
				stage.setOnCloseRequest(closeHandler);
			}
			
			/*stage.setOnCloseRequest(e -> {
				Platform.exit();
				System.exit(0);
			});*/
			if (maximized) {
				stage.setMaximized(true);
			} else {
				stage.centerOnScreen();
			}
			if (title!=null) {
				stage.setTitle(title);
			}
			stage.show();
		} catch (IOException e) {
			DialogHelper.showErrorDialog(e);
		}
	}

}
