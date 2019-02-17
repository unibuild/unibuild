package net.unibld.client.desktop.service;

import java.io.File;

import net.unibld.core.security.IPasswordReaderInterface;

public interface IClientGoalRunnerUI {

	IPasswordReaderInterface getPasswordDialog();

	void clearConsole();

	void createErrorLogTab(File f);

	void setTaskStarted(int idx);
	void setTaskFailed(int idx);
	void setTaskSuccess(int idx);

	void resetTasksAll();
}
