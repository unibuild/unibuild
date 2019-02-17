package net.unibld.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.unibld.core.persistence.model.SecretStore;
import net.unibld.core.repositories.SecretStoreRepository;

@Service
public class SecretStoreServiceImpl implements SecretStoreService {
	@Autowired
	private SecretStoreRepository storeRepo;
	
	@Override
	public String getValue(String key) {
		SecretStore row = storeRepo.findOne(key);
		if (row==null) {
			return null;
		}
		return row.getValue();
	}

	@Override
	@Transactional
	public void putValue(String key, String value) {
		SecretStore row = storeRepo.findOne(key);
		if (row==null) {
			row=new SecretStore();
			row.setKey(key);
		}
		row.setValue(value);
		storeRepo.save(row);
	}

	@Override
	@Transactional
	public void deleteAll() {
		storeRepo.deleteAll();
	}

	@Override
	public boolean isKeyExisting(String key) {
		return storeRepo.findOne(key)!=null;
	}

	@Override
	@Transactional
	public void deleteKey(String key) {
		storeRepo.delete(key);
	}

}
