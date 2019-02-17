package net.unibld.core.remote;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "buildStarted")
public class BuildStartedDto {
	@Attribute(name="startTime",required=false)
	private String startTime;
	@Attribute(name="started")
	private boolean started;
	
	@Element(name="errorMessage",required=false)
	private String errorMessage;
	@Attribute(name="id")
	private String id;
	
	public boolean isStarted() {
		return started;
	}
	public void setStarted(boolean started) {
		this.started = started;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
		
}
