package net.unibld.core.service;

public interface SecretStoreService {
	public String getValue(String key);
	public void putValue(String key,String value);
	public void deleteAll();
	public boolean isKeyExisting(String key);
	public void deleteKey(String key);
}
