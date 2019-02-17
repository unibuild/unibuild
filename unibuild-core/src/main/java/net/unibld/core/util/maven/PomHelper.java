package net.unibld.core.util.maven;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.unibld.core.task.ITaskLogger;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomHelper {
	private static final Logger LOG=LoggerFactory.getLogger(PomHelper.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getVersionFromPomFile(String pomFile,ITaskLogger logger) throws IOException, DocumentException {
		File f=new File(pomFile);
		if (!f.exists()||!f.isFile()) {
			throw new IllegalArgumentException("Invalid POM file: "+pomFile);
		}
		String xml=FileUtils.readFileToString(f, "UTF-8");
		Document document = DocumentHelper.parseText(xml);
		
		Map uris = new HashMap();
		uris.put("p", "http://maven.apache.org/POM/4.0.0");
		XPath xpath = document.createXPath("//p:project/p:version");
		xpath.setNamespaceURIs(uris);
		
		Element versionNode = (Element) xpath.selectSingleNode(document);
		
		XPath xpath2 = document.createXPath("//p:project/p:parent/p:version");
		xpath2.setNamespaceURIs(uris);
		
		Element parentVersionNode = (Element) xpath2.selectSingleNode(document);
		if (versionNode==null&&parentVersionNode==null) {
			throw new IllegalStateException("No version info could be found in POM: "+pomFile);
		}
		
		if (versionNode!=null) {
			String msg = String.format("Project version %s detected in POM: %s",versionNode.getText(),pomFile);
			LOG.info(msg);
			if (logger!=null) {
				logger.logInfo(msg);
			}
			return versionNode.getText();
		}
		if (parentVersionNode!=null) {
			String msg = String.format("Parent project version %s detected in POM: %s",parentVersionNode.getText(),pomFile);
			LOG.info(msg);
			if (logger!=null) {
				logger.logInfo(msg);
			}
			return parentVersionNode.getText();
		}
		throw new IllegalStateException("No version info could be read from POM: "+pomFile);
	}
}
