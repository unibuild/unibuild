package net.unibld.client.desktop.controller;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.unibld.client.desktop.SpringBeanFactory;
import net.unibld.client.desktop.service.ClientWorkspaceManager;
import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Project;

public class ProjectBrowser {

	private TreeView<String> treeView;

	public ProjectBrowser(TreeView<String> projectBrowserView) {
		this.treeView = projectBrowserView;
	}

	public void load() {
		ClientWorkspaceManager ws = SpringBeanFactory.getBean(ClientWorkspaceManager.class);

		TreeItem<String> root = new TreeItem<String>("Recent projects");
		root.setExpanded(true);

		for (Project p : ws.getProjectService().getAvailableProjectsOrderByName()) {
			TreeItem<String> projectItem = new TreeItem<String>(p.getName());
			root.getChildren().add(projectItem);
			
		}

		treeView.setRoot(root);
	}

}
