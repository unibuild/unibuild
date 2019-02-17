package net.unibld.client.desktop.util.javafx;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.unibld.core.util.PlatformHelper;

/**
 * Helper class related to Java FX message boxes and dailogs
 * @author andor
 *
 */
public class DialogHelper {
	/**
	 * Wrapper class that wraps a Java FX stage and a controller for a dialog
	 * @author andor
	 *
	 */
	public static class DialogWrapper {
		private Stage stage;
		private Object controller;
		/**
		 * @return Stage
		 */
		public Stage getStage() {
			return stage;
		}
		/**
		 * @param stage Stage
		 */
		public void setStage(Stage stage) {
			this.stage = stage;
		}
		/**
		 * @return Controller object
		 */
		public Object getController() {
			return controller;
		}
		/**
		 * @param controller Controller object
		 */
		public void setController(Object controller) {
			this.controller = controller;
		}
		
		/**
		 * Shows the underlying stage
		 */
		public void show() {
			this.stage.show();
		}
		
	}

	private static final Logger LOGGER=LoggerFactory.getLogger(DialogHelper.class);
	
	/**
	 * Shows an information type message box
	 * @param title Title
	 * @param header Header text
	 * @param text Text
	 * @param autoClose True if the dialog should close automatically after OK is pressed
	 * @param listener Listener for the OK pressed event or null
	 */
	public static void showMessageBox(String title,String header,String text,boolean autoClose,MessageBoxListener listener) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(text);
		
		DialogHelper.applyCustomStyle(alert.getDialogPane(),header,"/images/icons/Warning.png");
		DialogHelper.applyButtonStyles(alert,"OK");
		alert.showAndWait().ifPresent(rs -> {
		    if (rs == ButtonType.OK) {
		    	
		        LOGGER.info("Pressed OK.");
		        if (autoClose) {
		        	alert.close();
		        }
		        if (listener!=null) {
		        	try {
						listener.okPressed();
					} catch (IOException e) {
						LOGGER.error("Failed to execute listener method",e);
					}
		        }
		    }
		});
		
	}
	
	/**
	 * Shows an information type message box that automatically closes after OK is pressed
	 * @param title Title
	 * @param header Header text
	 * @param text Text
	 */
	public static void showMessageBox(String title,String header,String text) {
		showMessageBox(title, header, text, true, null);
	}

	/**
	 * Uncaught exception handler for the application
	 * @param thr Thread
	 * @param e Throwable
	 */
	public static void showErrorDialog(Thread thr,Throwable e) {
		showErrorDialog(e);
	}
	
	/**
	 * Shows an error dialog for a {@link Throwable}
	 * @param e Throwable
	 */
	public static void showErrorDialog(Throwable e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		LOGGER.info("Exception stack trace for the error: ...", e);
		Throwable prev = e;
		Throwable prevCause = prev.getCause();
		// get root cause in release mode, print stack trace in debug mode (-DdebugMode=true VM arg)
		if (System.getProperty("debugMode") == null) {
			if (prevCause != null) {
				while (prevCause.getCause() != null) {
					prev = prevCause;
					prevCause = prev.getCause();
				}
			}
			alert.setHeaderText(limitTo(prev.getMessage(),50));
			if (prevCause != null) {
				alert.setContentText(limitTo(prevCause.getMessage(),100));
			} else {
				alert.setContentText(limitTo(prev.getMessage(),100));
			}
		}
		else {
			alert.setHeaderText("An error occured");
			alert.setContentText(limitTo(ExceptionUtils.getStackTrace(e),100));
		}
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}
	private static String limitTo(String str,int limit) {
		if (str==null) {
			return null;
		}
		if (str.length()>limit) {
			return str.substring(0,limit)+"...";
		}
		return str;
	}


	/**
	 * Shows an error dialog with the specified header and content text
	 * @param header Header text
	 * @param text Content text
	 */
	public static void showErrorDialog(String header,String text) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		if (header!=null) {
			alert.setHeaderText(header);
		}
		if (text!=null) {
			alert.setContentText(text);
		}
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

	public static DialogWrapper createFxmlDialog(String fxmlFileName, String title) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(DialogHelper.class.getResource("/fxml/"+fxmlFileName));
	    Parent root1 = (Parent) fxmlLoader.load();
	    Stage stage = new Stage();
	    stage.initModality(Modality.APPLICATION_MODAL);
	    if (PlatformHelper.isMac()) {
	    	stage.initStyle( StageStyle.DECORATED);
	    } else {
	    	stage.initStyle( StageStyle.UTILITY);
	    }
	    stage.setResizable(false);
	    stage.setTitle(title);
	    Scene scene = new Scene(root1);
	    //scene.getStylesheets().add(SceneHelper.class.getResource(ApplicationConstants.CSS_PATH).toExternalForm());

		stage.setScene(scene);  
	    
	    DialogWrapper wr=new DialogWrapper();
	    wr.setStage(stage);
	    wr.setController(fxmlLoader.getController());
	    return wr;
	}

	public static void showFxmlDialog(String fxmlFileName, String title) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(DialogHelper.class.getResource("/fxml/"+fxmlFileName));
	    Parent root1 = (Parent) fxmlLoader.load();
	    Stage stage = new Stage();
	    stage.initModality(Modality.APPLICATION_MODAL);
	    stage.initStyle(StageStyle.UNDECORATED);
	    stage.setTitle(title);
	    Scene scene = new Scene(root1);
	    //scene.getStylesheets().add(SceneHelper.class.getResource(ApplicationConstants.CSS_PATH).toExternalForm());

		stage.setScene(scene);  
	    stage.show();
	}

	public static void applyButtonStyles(Alert alert,String positiveLabel) {
		ButtonBar buttonBar = (ButtonBar)alert.getDialogPane().lookup(".button-bar");
		//buttonBar.setStyle("-fx-font-size: 24px;"
	    //       + "-fx-background-color: indianred;");
		for (Node n:buttonBar.getButtons()) {
			if (n instanceof Button) {
				Button b=(Button) n;
				if (b.getText().equals(positiveLabel)) {
					b.setStyle("-fx-background-color: #e05129; -fx-base: #e05129;");
				} else {
					b.setStyle("-fx-background-color: #000000; -fx-base: #000000;");
				}
				b.setPrefHeight(50);
				b.setPrefWidth(120);
			}
		}

	}
	
	public static void applyCustomStyle(DialogPane dialogPane,String headerText,String imagePath) {
		
		//dialogPane.getStylesheets().add(
		//		   DialogHelper.class.getResource(ApplicationConstants.CSS_PATH).toExternalForm());

				
		
	    GridPane grid = new GridPane();
	    ColumnConstraints graphicColumn = new ColumnConstraints();
	    graphicColumn.setFillWidth(false);
	    graphicColumn.setHgrow(Priority.NEVER);
	    ColumnConstraints textColumn = new ColumnConstraints();
	    textColumn.setFillWidth(true);
	    textColumn.setHgrow(Priority.ALWAYS);
	    grid.getColumnConstraints().setAll(graphicColumn, textColumn);
	    grid.setPadding(new Insets(5));

	    Image image1 = new Image( DialogHelper.class.getResourceAsStream(imagePath));
	    ImageView imageView = new ImageView(image1);
	    imageView.setFitWidth(64);
	    imageView.setFitHeight(64);
	    StackPane stackPane = new StackPane(imageView);
	    stackPane.setAlignment(Pos.CENTER);
	    grid.add(stackPane, 0, 0);

	    Label headerLabel = new Label(headerText);
	    headerLabel.setWrapText(true);
	    headerLabel.setAlignment(Pos.CENTER_LEFT);
	    headerLabel.setMaxWidth(Double.MAX_VALUE);
	    headerLabel.setMaxHeight(Double.MAX_VALUE);
	    headerLabel.setStyle("-fx-font-size: 18;");
	    grid.add(headerLabel, 1, 0);

	    dialogPane.setHeader(grid);
	    dialogPane.setGraphic(null);
	}
	
	
	public static Optional<ButtonType> showConfirmDialog(Stage mainStage,String headerText,String text,String positiveLabel) {
		Alert alert = createConfirmDialog(mainStage, headerText, text, positiveLabel);

        Optional<ButtonType> closeResponse = alert.showAndWait();
		return closeResponse;
	}

	public static Alert createConfirmDialog(Stage mainStage, String headerText, String text, String positiveLabel) {
		Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                text
        );
        Button exitButton = (Button) alert.getDialogPane().lookupButton(
                ButtonType.OK
        );
        Button cancelButton = (Button) alert.getDialogPane().lookupButton(
                ButtonType.CANCEL
        );
        exitButton.setText(positiveLabel);
        cancelButton.setText("CANCEL");
        alert.setHeaderText(headerText);
        DialogHelper.applyCustomStyle(alert.getDialogPane(),headerText,"/images/icons/Warning.png");
		
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(mainStage);

        // normally, you would just use the default alert positioning,
        // but for this simple sample the main stage is small,
        // so explicitly position the alert so that the main window can still be seen.
        //closeConfirmation.setX(mainStage.getX());
        //closeConfirmation.setY(mainStage.getY() + mainStage.getHeight());
        
        DialogHelper.applyButtonStyles(alert, positiveLabel);
		return alert;
	}
}
