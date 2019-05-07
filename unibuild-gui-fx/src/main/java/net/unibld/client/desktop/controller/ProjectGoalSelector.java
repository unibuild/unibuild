package net.unibld.client.desktop.controller;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import net.unibld.client.desktop.BuildDirHelper;
import net.unibld.client.desktop.SpringBeanFactory;
import net.unibld.client.desktop.service.ClientWorkspaceManager;
import net.unibld.core.BuildTask;
import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.task.TaskRegistry;

/**
 * A class that handles project and goal selections.
 * @author andor
 *
 */
public class ProjectGoalSelector {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGoalSelector.class);
	
	static class TaskState {
		private BuildTask task;
		private ObjectProperty<Image> image;
		
		public TaskState(BuildTask task, ObjectProperty<Image> image) {
			super();
			this.task = task;
			this.image = image;
		}
		public BuildTask getTask() {
			return task;
		}
		public void setTask(BuildTask task) {
			this.task = task;
		}
		public ObjectProperty<Image> getImage() {
			return image;
		}
		public void setImage(ObjectProperty<Image> image) {
			this.image = image;
		}
	}
	
	
	
	private Project selectedProject;
	private ObservableList<String> selectedProjectGoals=FXCollections.observableArrayList();
	private SimpleStringProperty projectNameProperty=new SimpleStringProperty("-");
	private SimpleStringProperty projectPathProperty=new SimpleStringProperty("");
	private SimpleStringProperty buildDirProperty=new SimpleStringProperty("");
	
	private SimpleStringProperty projectLabelProperty=new SimpleStringProperty("");
	private ProjectConfig selectedProjectConfig;

	private ObservableList<TreeItem<String>> projectInspectorGoals;
	
	private ObservableList<GoalTaskItem> selectedGoalTasks=FXCollections.observableArrayList();
	private Map<Integer,TaskState> taskStatesByIndex=new HashMap<>();
	
	private ObjectProperty<String> selectedGoalProperty=new SimpleObjectProperty<String>("");

	private MainController controller;
	
	public ProjectGoalSelector(MainController c) {
		this.controller=c;
	}
	
	
	public void selectProject(Project p) {
		boolean sameProject=selectedProject!=null && p!=null && selectedProject.getId().equals(p.getId());
		String selectedGoal = getSelectedGoal();
		this.selectedProject=p;
		this.selectedProjectGoals.clear();
		if (projectInspectorGoals!=null) {
			projectInspectorGoals.clear();
		}
		
		if (this.selectedProject!=null) {
			LOGGER.info("Project selected: {} ({})",p.getName(),p.getPath());
			ClientWorkspaceManager ws = SpringBeanFactory.getBean(ClientWorkspaceManager.class);

			selectedProjectConfig = ws.loadProjectConfig(selectedProject);
			if (selectedProjectConfig != null && selectedProjectConfig.getGoalsConfig()!=null) {
				List<String> goals=new ArrayList<>();
				
				if (selectedProjectConfig.getGoalsConfig().getGoals()!=null) {
					for (BuildGoalConfig g:selectedProjectConfig.getGoalsConfig().getGoals()) {
						goals.add(g.getName());
					}
				}
				if (!goals.isEmpty()) {
					Collections.sort(goals,Collator.getInstance());
					selectedProjectGoals.addAll(goals);
					
					if (projectInspectorGoals!=null) {
						for (String goal:goals) {
							projectInspectorGoals.add(new TreeItem<String>(goal));
						}
					}
				}
			}
			projectNameProperty.set(selectedProject.getName());
			projectPathProperty.set(selectedProject.getPath());
			projectLabelProperty.set("Project: "+selectedProject.getName());
			buildDirProperty.set(BuildDirHelper.getBuildPath(selectedProject.getName()));
			
			try {
				controller.setEditorContents(FileUtils.readFileToString(new File(selectedProject.getPath()), "UTF-8"));
			} catch (IOException e) {
				LOGGER.error("Failed to load project XML to editor",e);
			}
			
			if (sameProject) {
				selectGoal(selectedGoal);
			} else {
				selectGoal(null);
			}
		} else {
			projectNameProperty.set("-");
			projectPathProperty.set("");
			buildDirProperty.set("");
			projectLabelProperty.set("No project selected.");
			selectGoal(null);
			controller.setEditorContents("");
		}
		
	}


	public Project getSelectedProject() {
		return selectedProject;
	}


	public ObservableList<String> getSelectedProjectGoals() {
		return selectedProjectGoals;
	}


	public SimpleStringProperty getProjectNameProperty() {
		return projectNameProperty;
	}


	public SimpleStringProperty getProjectPathProperty() {
		return projectPathProperty;
	}


	public SimpleStringProperty getProjectLabelProperty() {
		return projectLabelProperty;
	}


	public void setProjectInspectorGoals(ObservableList<TreeItem<String>> children) {
		this.projectInspectorGoals=children;
	}


	public ProjectConfig getSelectedProjectConfig() {
		return selectedProjectConfig;
	}


	public SimpleStringProperty getBuildDirProperty() {
		return buildDirProperty;
	}
	
	
	
	public void selectGoal(String goal) {
		this.selectedGoalTasks.clear();
		this.taskStatesByIndex.clear();
		if (goal!=null) {
			LOGGER.info("Goal selected: {}",goal);
			selectedGoalProperty.set(goal);
			if (getSelectedProjectConfig()!=null) {
				List<BuildTask> tasks=null;
				
				List<BuildGoalConfig> goals=getSelectedProjectConfig().getGoalsConfig().getGoals();
				for (BuildGoalConfig g:goals) {
					if (g.getName().equals(goal)) {
						tasks=g.getTasks().getTasks();
					}
				}
			
				
				if (tasks!=null) {
					int idx=0;
					for (BuildTask task:tasks) {
						taskStatesByIndex.put(idx, new TaskState(task, new SimpleObjectProperty<Image>(Icons.getWaitingIcon())));
						selectedGoalTasks.add(new GoalTaskItem(idx,getTaskName(task)));
						idx++;
					}
				}
			}
		} else {
			LOGGER.info("No goal selected");
			selectedGoalProperty.set("");
		}
	}
	
	private String getTaskName(BuildTask task) {
		return SpringBeanFactory.getBean(TaskRegistry.class).getTaskNameByClass(task.getClass());
	}
	

	public String getSelectedGoal() {
		return selectedGoalProperty.get();
	}

	public ObjectProperty<String> getSelectedGoalProperty() {
		return selectedGoalProperty;
	}

	public ObservableList<GoalTaskItem> getSelectedGoalTasks() {
		return selectedGoalTasks;
	}

	public ObservableValue<Image> getTaskImage(int idx) {
		TaskState state = taskStatesByIndex.get(idx);
		if (state==null) {
			LOGGER.warn("Task state not found: {}",idx);
			return null;
		} else {
			return state.getImage();
		}
		
	}

	public void setTaskStarted(int idx) {
		TaskState state = taskStatesByIndex.get(idx);
		if (state==null) {
			LOGGER.warn("Task state not found: {}",idx);
		} else {
			state.getImage().set(Icons.getRunningIcon());
		}
		
	}
	
	public void setTaskSuccess(int idx) {
		TaskState state = taskStatesByIndex.get(idx);
		if (state==null) {
			LOGGER.warn("Task state not found: {}",idx);
		} else {
			state.getImage().set(Icons.getDoneIcon());
		}
		
	}
	
	public void setTaskFailed(int idx) {
		TaskState state = taskStatesByIndex.get(idx);
		if (state==null) {
			LOGGER.warn("Task state not found: {}",idx);
		} else {
			state.getImage().set(Icons.getFailedIcon());
		}
		
	}

	public void resetTasksAll() {
		for (TaskState s:taskStatesByIndex.values()) {
			s.getImage().set(Icons.getWaitingIcon());
		}
	}
}
