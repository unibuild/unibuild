package net.unibld.client.desktop.controller;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.unibld.client.desktop.SpringBeanFactory;
import net.unibld.client.desktop.service.ClientWorkspaceManager;

public class ProjectInspector {
	private TreeView<String> treeView;

	public ProjectInspector(TreeView<String> projectInspectorView) {
		this.treeView=projectInspectorView;
		
	}
	
	public void init(ProjectGoalSelector selector) {
		ClientWorkspaceManager ws = SpringBeanFactory.getBean(ClientWorkspaceManager.class);
		
		TreeItem<String> root = new TreeItem<String>();
	    root.setExpanded(true);
	    root.valueProperty().bind(selector.getProjectLabelProperty());
	    treeView.setRoot(root);
	    selector.setProjectInspectorGoals(root.getChildren());
	}

}
