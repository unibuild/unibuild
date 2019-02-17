package net.unibld.core;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A class that represents a special directory of a server, such as config
 * directory, lib directory or cache folders.
 * @author andor
 * @version 1.0
 * @updated 22-05.-2013 3:47:31
 */
@XmlRootElement(name="serverDirectory")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerDirectory implements Serializable {
	@XmlAttribute(required=true)
	private String path;
	
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	

	
}//end ServerDirectory