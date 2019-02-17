package net.unibld.client.desktop.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import net.unibld.client.desktop.util.javafx.JavaFXController;

public class ConsoleController extends JavaFXController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleController.class);
	
	
	@FXML
	private ScrollPane consoleScroll;
	
	@FXML
	private TextArea consoleText;


	private Timer timer;


	private MessageConsole console;

	
	
	@FXML
	private void initialize() {
		consoleText.setText("");
		
		console = new MessageConsole(consoleText);
        console.redirectOut();
        console.redirectErr(Color.RED, null);
	}



	public void startConsoleWatch() {
		timer = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (console!=null) {
					console.flushStreams();
				}
				
			}
		});
		timer.setRepeats(true);
		timer.setInitialDelay(50);
		timer.start(); 
		LOGGER.info("Console watch timer started");
	}
	
	public void stopConsoleWatch() {
		if (timer!=null&&timer.isRunning()) {
			timer.stop();
		}
		timer=null;
		
		if (console!=null) {
			console.flushStreams();
		}
		
	}


	public void addConsoleMessageLine(String msg) {
		if (msg==null||msg.trim().length()==0) {
			LOGGER.warn("Message was empty");
			return;
		}
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				consoleText.appendText("\r\n"+msg);
			}
		});
		
	}

	
	public void clearConsole() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				consoleText.setText("");
			}
		});
		
	}
	


	
	
}
