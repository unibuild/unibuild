package net.unibld.server.entities.build;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import net.unibld.server.entities.log.LogLevel;

@Entity
@Table(name = "build_log",
	indexes = {@Index(name = "IDX_buildlog_build",  columnList="buildId", unique = false)})
public class BuildLog implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;

	@Column(name = "log_level", nullable = false,length=10)
    @Enumerated(EnumType.STRING)
	private LogLevel level;
	
	@Column(name = "log_message", nullable=false)
	@Lob
	private String message;
	
	@Column(name="log_date",nullable = false)
	private Date date;
	
	@Column(name="build_id", nullable=false)
	private String buildId;
	
	
	
	public LogLevel getLevel() {
		return level;
	}
	public void setLevel(LogLevel level) {
		this.level = level;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void append(String str) {
		if (message!=null && str!=null) {
			message+=str;
		} else if (str!=null) {
			message=str;
		}
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBuildId() {
		return buildId;
	}
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
	
}
