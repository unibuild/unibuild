package net.unibld.core.security;

import java.io.Serializable;

/**
 * A key to uniquely identify an identity in the credential store.
 * @author andor
 *
 */
public class CredentialKey implements Serializable {
	
	private static final long serialVersionUID = -6175062875966702658L;
	private String type;
	private String target;
	private String identity;
	/**
	 * @return Credential type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type Credential type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * Constructor with a credential type, target and identity
	 * @param type Credential type
	 * @param target Credential target
	 * @param identity Identity of the credential
	 */
	public CredentialKey(String type, String target, String identity) {
		super();
		this.type = type;
		this.target = target;
		this.identity = identity;
	}
	/**
	 * @return Credential target
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target Credential target
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return Identity of the credential
	 */
	public String getIdentity() {
		return identity;
	}
	/**
	 * @param identity Identity of the credential
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identity == null) ? 0 : identity.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CredentialKey other = (CredentialKey) obj;
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
