package net.unibld.client.desktop.controller;



import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import net.unibld.client.desktop.ApplicationHolder;
import net.unibld.client.desktop.SpringBeanFactory;
import net.unibld.client.desktop.async.GoalRunnerService;
import net.unibld.client.desktop.service.ClientGoalRequest;
import net.unibld.client.desktop.service.ClientGoalResult;
import net.unibld.client.desktop.service.ClientWorkspaceManager;
import net.unibld.client.desktop.service.IClientGoalRunnerUI;
import net.unibld.client.desktop.util.DesktopApi;
import net.unibld.client.desktop.util.javafx.DialogHelper;
import net.unibld.client.desktop.util.javafx.JavaFXController;
import net.unibld.client.desktop.util.javafx.ToastHelper;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.security.IPasswordReaderInterface;
import net.unibld.core.service.ProjectService;

public class MainController  extends JavaFXController implements IClientGoalRunnerUI {
	private class GoalTaskCell extends ListCell<GoalTaskItem> {
        private ImageView imageView = new ImageView();

        @Override
        protected void updateItem(GoalTaskItem item, boolean empty) {
            super.updateItem(item, empty);
            
            imageView.imageProperty().unbind();

            if (empty || item == null) {
                imageView.setImage(null);

                setGraphic(null);
                setText(null);
            } else {
                imageView.imageProperty().bind(
                        selector.getTaskImage(item.getIdx()));

                setText(item.getName());
                setGraphic(imageView);
            }
        }
    }

	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
	
	@FXML
	private Menu fileMenu;
	@FXML
	private Menu editMenu;
	@FXML
	private Menu projectMenu;
	@FXML
	private Menu helpMenu;
	
	@FXML
	private TabPane mainTabPane;
	
	
	@FXML
	private ConsoleController consoleController;
	@FXML
	private EditorController editorController;

	
	@FXML
	private Button startButton;
	
	@FXML
	private Button stopButton;
	
	@FXML
	private Label tasksLabel;
	
	@FXML
	private ListView<GoalTaskItem> tasksList;
	
	@FXML
	private TreeView<String> projectInspectorView;
	
	@FXML
	private TreeView<String> projectBrowserView;
	
	@FXML
	private ChoiceBox<String> goalChoiceBox;
	@FXML
	private Label projectLabel;
	@FXML
	private Label projectPathLabel;
	
	@FXML
	private ProgressBar progressBar;
	
	
	@FXML
	private TextField buildDirText;
	
	@FXML
	private CheckBox traceEnabledCheckbox;
	
	@FXML
	private MenuBar menuBar;

	private ProjectBrowser projectBrowser;

	private ProjectInspector projectInspector;
	private ProjectGoalSelector selector;

	
	private GoalProgressListener goalProgressListener;

	private PasswordManager passwordManager;

	private GoalRunnerService service;

	
	@FXML
	private void initialize() {
		progressBar.setVisible(false);
		
		this.selector=new ProjectGoalSelector(this);
		
		this.projectLabel.textProperty().bind(selector.getProjectNameProperty());
		this.projectPathLabel.textProperty().bind(selector.getProjectPathProperty());
		this.buildDirText.textProperty().bind(selector.getBuildDirProperty());
		
		goalChoiceBox.itemsProperty().bind(new SimpleObjectProperty<ObservableList<String>>(selector.getSelectedProjectGoals()));
		goalChoiceBox.valueProperty().bindBidirectional(selector.getSelectedGoalProperty());
		
		tasksList.setCellFactory(param -> new GoalTaskCell());
		tasksList.itemsProperty().bind(new SimpleObjectProperty<ObservableList<GoalTaskItem>>(selector.getSelectedGoalTasks()));
		
		this.projectBrowser=new ProjectBrowser(projectBrowserView);
		this.projectBrowser.load();
		this.projectInspector=new ProjectInspector(projectInspectorView);
		
		ProjectService ps = SpringBeanFactory.getBean(ProjectService.class);
		
		Project lastProject = ps.getLastProject();
		String lastGoal = ps.getLastGoal();
		this.projectInspector.init(this.selector);
		
		selector.selectProject(lastProject);
		selector.selectGoal(lastGoal);
		
		this.goalProgressListener=new GoalProgressListener(this,progressBar);
		
		this.passwordManager=new PasswordManager(this);
		
	}
	
	@FXML
	public void showAboutDialog() {
		
	}

	@FXML
	public void startGoal() {
		if (selector.getSelectedProject()==null) {
			throw new IllegalStateException("No project selected");
		}
		if (selector.getSelectedGoal()==null) {
			throw new IllegalStateException("No goal selected");
		}
		
		consoleController.startConsoleWatch();
		LOGGER.info("Starting goal {} of project {}...",selector.getSelectedGoal(),selector.getSelectedProject());
		ClientGoalRequest req=new ClientGoalRequest();
		req.setProject(selector.getSelectedProject());
		req.setGoal(selector.getSelectedGoal());
		req.setUi(this);
		req.setBuildDir(buildDirText.getText());
		req.setTraceEnabled(traceEnabledCheckbox.isSelected());
		
		req.setProgressListener(goalProgressListener);
		
		setDisabledState(true);
		progressBar.setVisible(true);
		
		this.service=new GoalRunnerService(req);
		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	ClientGoalResult res=service.getValue();
		    	consoleController.stopConsoleWatch();
				
		    	setDisabledState(false);
		    	
		    	if (res.isSuccess()) {
			    	consoleController.addConsoleMessageLine("GOAL COMPLETED SUCCESSFULLY.");
					
			    	ToastHelper.showInfoMessage("Run success","Goal "+req.getGoal()+" completed successfully.");
		    	} else {
		    		DialogHelper.showErrorDialog("Run failed",res.getError());
		    	}
		    }
		});
		
		service.setOnFailed(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	//resetCursor();
				consoleController.stopConsoleWatch();
				
		    	setDisabledState(false);
		    	DialogHelper.showErrorDialog(service.getException());
		    }
		});
		service.start();
	}
	
	@FXML
	public void stopGoal() {
		if (this.service!=null) {
			LOGGER.info("Stopping goal...");
			service.cancel();
			setDisabledState(false);
		}
	}
	
	protected void setDisabledState(boolean disabled) {
		startButton.setDisable(disabled);
		stopButton.setDisable(!disabled);
		menuBar.setDisable(disabled);
		goalChoiceBox.setDisable(disabled);
		traceEnabledCheckbox.setDisable(disabled);
	}

	@FXML
	public void save() {
		
	}
	
	@FXML
	public void saveAs() {
		
	}
	
	@FXML
	public void openProject() {
		FileChooser chooser=new FileChooser();
		chooser.setTitle("Open project");
		FileChooser.ExtensionFilter fileExtensions =new FileChooser.ExtensionFilter("Project XML files","*.xml");
		chooser.getExtensionFilters().add(fileExtensions);
		File file=chooser.showOpenDialog(menuBar.getParent().getScene().getWindow());
		if (file!=null) {
			LOGGER.info("Importing project: {}...",file.getAbsolutePath());
			try {
				Project imp = SpringBeanFactory.getBean(ClientWorkspaceManager.class).importProject(file.getAbsolutePath());
				selector.selectProject(imp);
				
				//initAvailableProjects(availableProjects,imp,null);
			} catch (Exception ex) {
				LOGGER.error("Failed to import project",ex);
				DialogHelper.showErrorDialog("Project open error", ex.getMessage());
			}
		}
	}
	
	@FXML
	public void openBuildDir() {
		DesktopApi.browse(new File(buildDirText.getText()).toURI());
	}
	
	
	
	@FXML
	public void exit() {
		ApplicationHolder.getApplication().exitApplication();
	}

	@Override
	public IPasswordReaderInterface getPasswordDialog() {
		return passwordManager;
	}

	@Override
	public void clearConsole() {
		consoleController.clearConsole();
	}

	@Override
	public void createErrorLogTab(File f) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					String log = FileUtils.readFileToString(f, "UTF-8");
					Tab tab = new Tab(f.getName());
					tab.setClosable(true);
					
					GridPane grid = new GridPane();
					
					RowConstraints rc = new RowConstraints();
					rc.setVgrow(Priority.ALWAYS);
					rc.setMinHeight(600);
					grid.getRowConstraints().add(rc);
					
					ColumnConstraints cc = new ColumnConstraints();
					cc.setHgrow(Priority.ALWAYS);
					cc.setMinWidth(800);
					grid.getColumnConstraints().add(cc);
					
					ScrollPane scroll=new ScrollPane();
					scroll.setFitToWidth(true);
					GridPane.setRowIndex(scroll, 0);
					grid.getChildren().add(scroll);
					
					TextArea text=new TextArea();
					text.setEditable(false);
					text.setMinHeight(599);
					text.setMinWidth(798);
					text.setWrapText(true);
					text.setText(log);
					scroll.setContent(text);
					
					tab.setContent(grid);
					mainTabPane.getTabs().add(tab);
				} catch (IOException ex) {
					LOGGER.error("Failed to read log file: "+f.getAbsolutePath(),ex);
					throw new IllegalStateException("Failed to read log file: "+f.getAbsolutePath(),ex);
				}
			}
		});
		
	}

	@Override
	public void setTaskStarted(int idx) {
		selector.setTaskStarted(idx);
	}

	@Override
	public void setTaskFailed(int idx) {
		selector.setTaskFailed(idx);
	}

	@Override
	public void setTaskSuccess(int idx) {
		selector.setTaskSuccess(idx);
	}
	
	/*@FXML
	public void showFindCoordinatesDialog() throws IOException {
		DialogHelper.DialogWrapper dw = DialogHelper.createFxmlDialog("FindCoordinates.fxml","Find coordinates");
		((FindCoordinatesController)dw.getController()).setMapController(mapController);
		dw.show();
	}*/
	
	@FXML
	public void reloadProject() {
		if (selector.getSelectedProject()!=null) {
			LOGGER.info("Reloading project: {}...",selector.getSelectedProject().getName());
			selector.selectProject(selector.getSelectedProject());
			ToastHelper.showInfoMessage("Project reload", 
					String.format("Project %s reloaded successfully.", selector.getSelectedProject().getName()));
		}
	}

	@Override
	public void resetTasksAll() {
		selector.resetTasksAll();	
	}

	public void setEditorContents(String xml) {
		editorController.setContents(xml);
	}
}
