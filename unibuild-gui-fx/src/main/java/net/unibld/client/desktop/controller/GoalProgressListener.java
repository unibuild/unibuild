package net.unibld.client.desktop.controller;

import javafx.scene.control.ProgressBar;
import net.unibld.core.build.ProgressListener;

public class GoalProgressListener implements ProgressListener {
	private ProgressBar progressBar;
	private MainController controller;
	public GoalProgressListener(MainController controller, ProgressBar bar) {
		this.progressBar=bar;
		this.controller=controller;
	}
	public void progress(String buildId,int perc,int taskIdx) {
		progressBar.setProgress(((double)perc)/((double)100));
		controller.setTaskSuccess(taskIdx-1);
		controller.setTaskStarted(taskIdx);
	}
}
