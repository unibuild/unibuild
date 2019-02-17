package net.unibld.client.desktop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.GUIState;
import de.felixroske.jfxsupport.PropertyReaderHelper;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Felix Roske
 */
public abstract class AbstractConfigurableJavaFxApplicationSupport extends Application {
    
    private static String[] savedArgs = new String[0];

    private static Class<? extends AbstractFxmlView> savedInitialView;

    protected static ConfigurableApplicationContext applicationContext;

    private static SplashScreen splashScreen;

    private static List<Image> icons = new ArrayList<>();
    private BooleanProperty appCtxLoaded = new SimpleBooleanProperty(false);

    public static Stage getStage() {
        return GUIState.getStage();
    }

    public static Scene getScene() {
        return GUIState.getScene();
    }

    @Override
    public void init() throws Exception {
        CompletableFuture.supplyAsync(() -> {
        	
        	SpringApplication app = new SpringApplication(new Object[]{this.getClass()});
        	
        	app.setWebEnvironment(false);
        	
    		ConfigurableApplicationContext ctx=app.run(savedArgs);
            
            List<String> fsImages = PropertyReaderHelper.get(ctx.getEnvironment(), "javafx.appicons");
            
            if (!fsImages.isEmpty()) {
                fsImages.forEach((s) -> icons.add(new Image(getClass().getResource(s).toExternalForm())));
            } 
            return ctx;
        }).thenAccept(this::launchApplicationView);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GUIState.setStage(stage);
        Stage splashStage = new Stage(StageStyle.UNDECORATED); 
        
        if(AbstractConfigurableJavaFxApplicationSupport.splashScreen.visible()) {
            Scene splashScene = new Scene(splashScreen.getParent());
            splashStage.setScene(splashScene);
//            splashStage.centerOnScreen();
            splashStage.show();
        }
        
        Runnable showMainAndCloseSplash = () -> {
            showInitialView();
            if(AbstractConfigurableJavaFxApplicationSupport.splashScreen.visible()) {
                splashStage.hide();
            }
        };
        
        synchronized(this) {
        if (appCtxLoaded.get() == true) {
            // Spring ContextLoader was faster
            Platform.runLater(showMainAndCloseSplash);
        } else {
            appCtxLoaded.addListener((ov, oVal, nVal) -> {
                Platform.runLater(showMainAndCloseSplash);
            });
        }
        }
        
    }

    private void showInitialView() {
        String stageStyle = applicationContext.getEnvironment().getProperty("javafx.stage.style");
        if(stageStyle != null) {
            GUIState.getStage().initStyle(StageStyle.valueOf(stageStyle.toUpperCase())); 
        }
        else {
            GUIState.getStage().initStyle(StageStyle.DECORATED);
        }
//        stage.hide();
        
        showView(savedInitialView);
    }
    
    private void launchApplicationView(ConfigurableApplicationContext ctx) {
        AbstractConfigurableJavaFxApplicationSupport.applicationContext = ctx;
        appCtxLoaded.set(true);
    }
    
    public static void showView(Class<? extends AbstractFxmlView> newView) {
        AbstractFxmlView view = applicationContext.getBean(newView);

        if (GUIState.getScene() == null) {
            GUIState.setScene(new Scene(view.getView()));
        } else {
            GUIState.getScene().setRoot(view.getView());
        }
        GUIState.getStage().setScene(GUIState.getScene());

        PropertyReaderHelper.setIfPresent(
                applicationContext.getEnvironment(),
                "javafx.title",
                String.class,
                AbstractConfigurableJavaFxApplicationSupport::setTitle);
        
        PropertyReaderHelper.setIfPresent(
                applicationContext.getEnvironment(),
                "javafx.stage.width",
                Double.class,
                GUIState.getStage()::setWidth);
        
        PropertyReaderHelper.setIfPresent(
                applicationContext.getEnvironment(),
                "javafx.stage.height",
                Double.class,
                GUIState.getStage()::setHeight);

        PropertyReaderHelper.setIfPresent(
                applicationContext.getEnvironment(), 
                "javafx.stage.resizable",
                Boolean.class,
                GUIState.getStage()::setResizable); 
        
        GUIState.getStage().getIcons().addAll(icons);
        GUIState.getStage().centerOnScreen();
        GUIState.getStage().show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (applicationContext != null) {
            applicationContext.close();
        } // else: someone did it already
    }

    protected static void setTitle(String title) {
        GUIState.getStage().setTitle(title);
    }

    protected static void launchApp(Class<? extends AbstractConfigurableJavaFxApplicationSupport> appClass,
            Class<? extends AbstractFxmlView> view, String[] args) {
       launchApp(appClass, view, new SplashScreen(), args); 
    }
    
    protected static void launchApp(Class<? extends AbstractConfigurableJavaFxApplicationSupport> appClass,
            Class<? extends AbstractFxmlView> view, SplashScreen splashScreen, String[] args) {
        savedInitialView = view;
        savedArgs = args;

        if(splashScreen != null) { 
            AbstractConfigurableJavaFxApplicationSupport.splashScreen = splashScreen;
        }
        else {
            AbstractConfigurableJavaFxApplicationSupport.splashScreen = new SplashScreen();
        }
        Application.launch(appClass, args);
    }

}
