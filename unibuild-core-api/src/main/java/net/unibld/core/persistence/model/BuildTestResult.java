package net.unibld.core.persistence.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="build_test_result")
public class BuildTestResult {
	/**
	 * Globally unique ID
	 */
	@Id
	@Column(nullable=false)
	private String id;

	/**
	 * Parent test
	 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private BuildTestSuite test; 
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	
	@PrePersist
	public void prePersist() {
		this.createDate=new Date();
	}
}
