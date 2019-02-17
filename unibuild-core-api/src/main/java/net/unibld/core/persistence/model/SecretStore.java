package net.unibld.core.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="secret_store")
public class SecretStore {
	@Id
	@Column(name="store_key")
	private String key;

	@Column(name="store_value",length=255,nullable=false)
	private String value;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
