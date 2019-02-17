package net.unibld.core.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for editing properties files without losing formatting, order and comments.
 * @author andor
 *
 */
public class PropertiesEditor {
	private static final Logger LOGGER=LoggerFactory.getLogger(PropertiesEditor.class);
	
	private String path;
	private Properties values;
	/**
	 * Constructor with a path
	 * @param path File path
	 * @param values Properties object
	 */
	public PropertiesEditor(String path,Properties values) {
		this.path=path;
		this.values=values;
	}
	
	/**
	 * Saves the edited properties to the underlying path.
	 * @param backup True if a backup should be created.
	 * @throws IOException If an I/O error occurs.
	 */
	public void save(boolean backup) throws IOException {
		File file=new File(path);
		if (!file.exists()||!file.isFile()) {
			throw new IllegalStateException("Properties file does not exist: "+path);
		}
		if (backup) {
			LOGGER.info("Backing up properties: {}...",path);
			FileUtils.copyFile(file, new File(file.getAbsolutePath()+".bkp"));
		}
		LOGGER.info("Saving properties: {}...",path);
		List<String> lines = FileUtils.readLines(file, "UTF-8");
		Enumeration<Object> keys = values.keys();
		while (keys.hasMoreElements()) {
			String key=(String) keys.nextElement();
			String val=values.getProperty(key);
			editProperty(lines,key,val);
		}
		
		FileUtils.writeLines(file, "UTF-8", lines);
		LOGGER.info("Successfully saved properties: {}...",file.getAbsolutePath());
	}

	private void editProperty(List<String> lines, String key, String val) {
		boolean found=findAndReplace(lines,key,val);
		if (found) {
			LOGGER.info("Edited property: {} -> {}",key,val);
			return;
		}
		
		found=findCommentedSectionAndAddProperty(lines,key,val);
		if (found) {
			LOGGER.info("Added property after comment section: {} -> {}",key,val);
			return;
		}
		
		addProperty(lines,key,val);
		LOGGER.info("Added property: {} -> {}",key,val);
		return;
	}

	private boolean findCommentedSectionAndAddProperty(List<String> lines,
			String key, String val) {
		int dot = key.indexOf(".");
		String section=null;
		if (dot==-1) {
			section=key;
		} else {
			section=key.substring(0,dot);
		}
		
		int lineIdx=0;
		int lastIdx=-1;
		for (String line:lines) {
			if (matchesSection(line,section)||matchesCommentedSection(line,section)) {
				lastIdx=lineIdx;
			}
			lineIdx++;
		}
		
		if (lastIdx==-1) {
			return false;
		}
		
		lines.add(lastIdx+1,String.format("%s=%s",key,val));
		LOGGER.info("Line added at {}: {}={}",lastIdx+1,key,val);
		return true;
	}

	private boolean matchesCommentedSection(String line, String section) {
		return line.matches(String.format("^#[ ]*%s\\..*=.*",section));
	}

	private boolean matchesSection(String line, String section) {
		return line.matches(String.format("^[ ]*%s\\..*=.*",section));
	}

	private boolean findAndReplace(List<String> lines, String key, String val) {
		int lineIdx=0;
		for (String line:lines) {
			if (matchesKey(line,key)) {
				lines.set(lineIdx, String.format("%s=%s",key,val));
				LOGGER.info("Line {} set: {}={}",lineIdx,key,val);
				return true;
			}
			lineIdx++;
		}
		return false;
	}

	

	private boolean matchesKey(String line, String key) {
		String repl=key.replace(".", "\\.");
		String regex=String.format("^%s[ ]*=.*",repl);
		return line.matches(regex);
	}

	private void addProperty(List<String> lines, String key, String val) {
		lines.add(String.format("%s=%s",key,val));
		LOGGER.info("Line added: {}={}",key,val);
	}
}
