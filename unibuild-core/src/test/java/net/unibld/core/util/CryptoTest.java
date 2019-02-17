package net.unibld.core.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;

public class CryptoTest {
	@Test
	public void testListAlgs() {
		Crypto.listAlgorithms(null);
	}
	@Test
	public void testEncryptAes() throws IOException {
		String password = UUID.randomUUID().toString();
		SecretKey key = Crypto.deriveKeyPad(password, 128);
		String str = Crypto.encrypt("contenttest123", key, null);
		SecretKey key2 = Crypto.deriveKeyPad(password, 128);
		
		String decr = Crypto.decryptNoSalt(str, key2);
		Assert.assertEquals(decr,"contenttest123");
	}
	/*@Test
	public void testEncryptPkcs12() throws IOException {
		byte[] salt = Crypto.generateSalt();
		SecretKey key = Crypto.deriveKeyPkcs12(salt,"password123", 128);
		String str = Crypto.encryptPkcs12("contenttest123", key, salt);
		//SecretKey key2 = Crypto.deriveKeyPkcs12(salt,"password123", 128);
		
		String decr = Crypto.decryptPkcs12(str, "password123");
		Assert.assertEquals(decr,"contenttest123");
	}*/
}
