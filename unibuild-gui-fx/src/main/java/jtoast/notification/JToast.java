package jtoast.notification;


import java.io.IOException;
import java.net.URL;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import jtoast.animations.AnimationProvider;
import jtoast.animations.AnimationType;
import jtoast.animations.JFade;
import jtoast.animations.JPop;
import jtoast.animations.JSlide;
import jtoast.animations.JTray;
import jtoast.stage.CustomStage;


public final class JToast {
	
	/**
	 * Toast fade timeout in millisecs
	 */
	public static final double TOAST_FADE_TIMEOUT_MS = 5000;

    @FXML
    private Label lblTitle, lblMessage, lblClose;
    @FXML
    private ImageView imageIcon;
    @FXML
    private Rectangle rectangleColor;
    @FXML
    private AnchorPane rootNode;

    private CustomStage stage;
    private NotificationType notificationType;
    private AnimationType animationType;
    private EventHandler<ActionEvent> onDismissedCallBack, onShownCallback;
    private JTray animator;
    private AnimationProvider animationProvider;
    
    private boolean autoHide=true;
    private Duration duration=new Duration(TOAST_FADE_TIMEOUT_MS);
	private Timeline hideTimer;

    /**
     * Initializes an instance of the tray notification object
     * @param title The title text to assign to the tray
     * @param body The body text to assign to the tray
     * @param img The image to show on the tray
     * @param rectangleFill The fill for the rectangle
     */
    public JToast(String title, String body, Image img, Paint rectangleFill) {
        initJToast(title, body, NotificationType.CUSTOM);

        setImage(img);
        setRectangleFill(rectangleFill);
    }

    /**
     * Initializes an instance of the tray notification object
     * @param title The title text to assign to the tray
     * @param body The body text to assign to the tray
     * @param notificationType The notification type to assign to the tray
     */
    public JToast(String title, String body, NotificationType notificationType ) {
        initJToast(title, body, notificationType);
    }

    /**
     * Initializes an empty instance of the tray notification
     */
    public JToast() {
        initJToast("", "", NotificationType.CUSTOM);
    }

    private void initJToast(String title, String message, NotificationType type) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TrayNotification.fxml"));

            fxmlLoader.setController(this);
            fxmlLoader.load();

            initStage();
            initAnimations();

            setTray(title, message, type);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAnimations() {

        animationProvider =
            new AnimationProvider(new JFade(stage), new JSlide(stage), new JPop(stage));

        //Default animation type
        setAnimationType(AnimationType.POPUP);
    }

    private void initStage() {

        stage = new CustomStage(rootNode, StageStyle.UNDECORATED);
        stage.setScene(new Scene(rootNode));
        stage.setAlwaysOnTop(true);
        stage.setLocation(stage.getBottomRight());

        lblClose.setOnMouseClicked(e -> dismiss());
        
        if (isAutoHide() && !duration.isIndefinite()) {
            hideTimer = new Timeline(new KeyFrame(duration));
            hideTimer.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    JToast.this.dismiss();
                }
            });
            hideTimer.playFromStart();
        }
    }

    public void setNotificationType(NotificationType nType) {

        notificationType = nType;

        URL imageLocation = null;
        String paintHex = null;

        switch (nType) {

            case INFORMATION:
                imageLocation = getClass().getResource("/images/jtoast/info.png");
                paintHex = "#65d0b6";
                break;

            case NOTICE:
                imageLocation = getClass().getResource("/images/jtoast/notice.png");
                paintHex = "#65d0b6";
                break;

            case SUCCESS:
                imageLocation = getClass().getResource("/images/jtoast/success.png");
                paintHex = "#009961";
                break;

            case WARNING:
                imageLocation = getClass().getResource("/images/jtoast/warning.png");
                paintHex = "#65d0b6";
                break;

            case ERROR:
                imageLocation = getClass().getResource("/images/jtoast/error.png");
                paintHex = "#65d0b6";
                break;

            case CUSTOM:
                return;
        }

        setRectangleFill(Paint.valueOf(paintHex));
        setImage(new Image(imageLocation.toString()));
        setTrayIcon(imageIcon.getImage());
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setTray(String title, String message, NotificationType type) {
        setTitle(title);
        setMessage(message);
        setNotificationType(type);
    }

    public void setTray(String title, String message, Image img, Paint rectangleFill, AnimationType animType) {
        setTitle(title);
        setMessage(message);
        setImage(img);
        setRectangleFill(rectangleFill);
        setAnimationType(animType);
    }

    public boolean isTrayShowing() {
        return animator.isShowing();
    }

    /**
     * Shows and dismisses the tray notification
     * @param dismissDelay How long to delay the start of the dismiss animation
     */
    public void showAndDismiss(Duration dismissDelay) {

        if (isTrayShowing()) {
            dismiss();
        } else {
            stage.show();

            onShown();
            animator.playSequential(dismissDelay);
        }

        onDismissed();
    }

    /**
     * Displays the notification tray
     */
    public void showAndWait() {

        if (! isTrayShowing()) {
            stage.show();

            animator.playShowAnimation();

            onShown();
        }
    }

    /**
     * Dismisses the notifcation tray
     */
    public void dismiss() {

        if (isTrayShowing()) {
            animator.playDismissAnimation();
            onDismissed();
        }
    }

    private void onShown() {
        if (onShownCallback != null)
            onShownCallback.handle(new ActionEvent());
    }

    private void onDismissed() {
        if (onDismissedCallBack != null)
            onDismissedCallBack.handle(new ActionEvent());
    }

    /**
     * Sets an action event for when the tray has been dismissed
     * @param event The event to occur when the tray has been dismissed
     */
    public void setOnDismiss(EventHandler<ActionEvent> event) {
        onDismissedCallBack  = event;
    }

    /**
     * Sets an action event for when the tray has been shown
     * @param event The event to occur after the tray has been shown
     */
    public void setOnShown(EventHandler<ActionEvent> event) {
        onShownCallback  = event;
    }

    /**
     * Sets a new task bar image for the tray
     * @param img The image to assign
     */
    public void setTrayIcon(Image img) {
        stage.getIcons().clear();
        stage.getIcons().add(img);
    }

    public Image getTrayIcon() {
        return stage.getIcons().get(0);
    }

    /**
     * Sets a title to the tray
     * @param txt The text to assign to the tray icon
     */
    public void setTitle(String txt) {
        lblTitle.setText(txt);
    }

    public String getTitle() {
        return lblTitle.getText();
    }

    /**
     * Sets the message for the tray notification
     * @param txt The text to assign to the body of the tray notification
     */
    public void setMessage(String txt) {
        lblMessage.setText(txt);
    }

    public String getMessage() {
        return lblMessage.getText();
    }

    public void setImage (Image img) {
        imageIcon.setImage(img);

        setTrayIcon(img);
    }

    public Image getImage() {
        return imageIcon.getImage();
    }

    public void setRectangleFill(Paint value) {
        rectangleColor.setFill(value);
    }

    public Paint getRectangleFill() {
        return rectangleColor.getFill();
    }

    public void setAnimationType(AnimationType type) {
        animator = animationProvider.findFirstWhere(a -> a.getAnimationType() == type);

        animationType = type;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

	public boolean isAutoHide() {
		return autoHide;
	}

	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}
}