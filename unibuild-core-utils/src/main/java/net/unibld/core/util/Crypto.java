package net.unibld.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class Crypto {
	public static class EncryptionException extends Exception {
		public EncryptionException(String msg) {
			super(msg);
		}
		public EncryptionException(String msg,Throwable cause) {
			super(msg,cause);
		}
	} 

    public static final String PKCS12_DERIVATION_ALGORITHM = "PBEWITHSHA256AND256BITAES-CBC-BC";
    public static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static String DELIMITER = "]";

    private static int KEY_LENGTH = 256;
    // minimum values recommended by PKCS#5, increase as necessary
    private static int ITERATION_COUNT = 1000;
    private static final int PKCS5_SALT_LENGTH = 8;

    private static SecureRandom random = new SecureRandom();

    private Crypto() {
    }
    
    public static void listAlgorithms(String algFilter) {
        Provider[] providers = Security.getProviders();
        for (Provider p : providers) {
            String providerStr = String.format("%s/%s/%f\n", p.getName(),
                    p.getInfo(), p.getVersion());
            System.out.println(providerStr);
            Set<Service> services = p.getServices();
            List<String> algs = new ArrayList<String>();
            for (Service s : services) {
                boolean match = true;
                if (algFilter != null) {
                    match = s.getAlgorithm().toLowerCase()
                            .contains(algFilter.toLowerCase());
                }

                if (match) {
                    String algStr = String.format("\t%s/%s/%s", s.getType(),
                            s.getAlgorithm(), s.getClassName());
                    algs.add(algStr);
                }
            }

            Collections.sort(algs);
            StringBuilder b=new StringBuilder();
            int i=0;
            for (String alg : algs) {
            	if (i>0) {
            		b.append(",");
            	}
            	b.append(alg);
                i++;
            }
           System.out.println("Available algorythms: "+b.toString());
            
        }
    }

    public static SecretKey deriveKeyPad(String password) {
        return deriveKeyPad(password,KEY_LENGTH);
    }

	public static SecretKey deriveKeyPad(String password,int keyLength) {
		try {
            long start = System.currentTimeMillis();
            byte[] keyBytes = new byte[keyLength / 8];
            // explicitly fill with zeros
            Arrays.fill(keyBytes, (byte) 0x0);

            // if password is shorter then key length, it will be zero-padded
            // to key length
            byte[] passwordBytes = password.getBytes("UTF-8");
            int length = passwordBytes.length < keyBytes.length ? passwordBytes.length
                    : keyBytes.length;
            System.arraycopy(passwordBytes, 0, keyBytes, 0, length);

            SecretKey result = new SecretKeySpec(keyBytes, "AES");
            long elapsed = System.currentTimeMillis() - start;
            //LOG.info(Crypto.class, String.format("Padding key derivation took %d [ms].",
            //        elapsed));

            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
	}

    // Illustration code only: don't use in production!
    public static SecretKey deriveKeySha1prng(String password) {
        try {
            long start = System.currentTimeMillis();
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] seed = password.getBytes("UTF-8");
            sr.setSeed(seed);
            kgen.init(KEY_LENGTH, sr);

            SecretKey result = kgen.generateKey();
            long elapsed = System.currentTimeMillis() - start;
            //LOG.debug(String.format("SHA1PRNG key derivation took %d [ms].",
                    //elapsed));

            return result;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public static SecretKey deriveKeyPkcs12(byte[] salt, String password) {
    	return deriveKeyPkcs12(salt, password, KEY_LENGTH);
    			
    }
    public static SecretKey deriveKeyPkcs12(byte[] salt, String password,int keyLength) {
        try {
            long start = System.currentTimeMillis();
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                    ITERATION_COUNT, keyLength);
            SecretKeyFactory keyFactory = SecretKeyFactory
                    .getInstance(PKCS12_DERIVATION_ALGORITHM);
            SecretKey result = keyFactory.generateSecret(keySpec);
            long elapsed = System.currentTimeMillis() - start;
            //LOG.debug(String.format("PKCS#12 key derivation took %d [ms].",
            //        elapsed));

            return result;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey deriveKeyPbkdf2(byte[] salt, String password) {
        try {
            long start = System.currentTimeMillis();
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                    ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory
                    .getInstance(PBKDF2_DERIVATION_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            //LOG.debug("key bytes: " + toHex(keyBytes));

            SecretKey result = new SecretKeySpec(keyBytes, "AES");
            long elapsed = System.currentTimeMillis() - start;
            //LOG.debug(String.format("PBKDF2 key derivation took %d [ms].",
            //        elapsed));

            return result;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);

        return b;
    }

    public static byte[] generateSalt() {
        byte[] b = new byte[PKCS5_SALT_LENGTH];
        random.nextBytes(b);

        return b;
    }

    public static String encryptPkcs12(String plaintext, SecretKey key,
            byte[] salt) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            PBEParameterSpec pbeSpec = new PBEParameterSpec(salt,
                    ITERATION_COUNT);
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeSpec);
            //LOG.debug("Cipher IV: " + toHex(cipher.getIV()));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            return String.format("%s%s%s", toBase64(salt), DELIMITER,
                    toBase64(cipherText));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String plaintext, SecretKey key, byte[] salt) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            byte[] iv = generateIv(cipher.getBlockSize());
            //LOG.debug("IV: " + toHex(iv));
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            //LOG.debug("Cipher IV: "
            //        + (cipher.getIV() == null ? null : toHex(cipher.getIV())));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            if (salt != null) {
                return String.format("%s%s%s%s%s", toBase64(salt), DELIMITER,
                        toBase64(iv), DELIMITER, toBase64(cipherText));
            }

            return String.format("%s%s%s", toBase64(iv), DELIMITER,
                    toBase64(cipherText));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String encrypt(byte[] raw, SecretKey key, byte[] salt) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            byte[] iv = generateIv(cipher.getBlockSize());
            //LogWrapper.getInstance().info(Crypto.class, "IV: " + toHex(iv));
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            //LogWrapper.getInstance().info(Crypto.class, "Cipher IV: "
            //        + (cipher.getIV() == null ? null : toHex(cipher.getIV())));
            byte[] cipherText = cipher.doFinal(raw);

            if (salt != null) {
                return String.format("%s%s%s%s%s", toBase64(salt), DELIMITER,
                        toBase64(iv), DELIMITER, toBase64(cipherText));
            }

            return String.format("%s%s%s", toBase64(iv), DELIMITER,
                    toBase64(cipherText));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } 
    }

    public static String toHex(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (byte b : bytes) {
            buff.append(String.format("%02X", b));
        }

        return buff.toString();
    }

    public static String toBase64(byte[] bytes) {
        //return Base64.encodeToString(bytes, Base64.NO_WRAP);
    	return Base64.encodeBytes(bytes);
    }

    public static byte[] fromBase64(String base64) throws IOException {
        //return Base64.decode(base64, Base64.NO_WRAP);
    	return Base64.decode(base64);
    }

    public static String decryptPkcs12(byte[] cipherBytes, SecretKey key,
            byte[] salt) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            PBEParameterSpec pbeSpec = new PBEParameterSpec(salt,
                    ITERATION_COUNT);
            cipher.init(Cipher.DECRYPT_MODE, key, pbeSpec);
            //LOG.debug("Cipher IV: " + toHex(cipher.getIV()));
            byte[] plainBytes = cipher.doFinal(cipherBytes);
            String plainrStr = new String(plainBytes, "UTF-8");

            return plainrStr;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(byte[] cipherBytes, SecretKey key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            //LOG.debug("Cipher IV: " + toHex(cipher.getIV()));
            byte[] plaintext = cipher.doFinal(cipherBytes);
            String plainrStr = new String(plaintext, "UTF-8");

            return plainrStr;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] decryptToBytes(byte[] cipherBytes, SecretKey key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            //LogWrapper.getInstance().info(Crypto.class, "Cipher IV: " + toHex(cipher.getIV()));
            byte[] plaintext = cipher.doFinal(cipherBytes);
            return plaintext;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } 
    }

    public static String decryptPkcs12(String ciphertext, String password) throws IOException {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 2) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }

        byte[] salt = fromBase64(fields[0]);
        byte[] cipherBytes = fromBase64(fields[1]);
        SecretKey key = deriveKeyPkcs12(salt, password);

        return decryptPkcs12(cipherBytes, key, salt);
    }

    public static String decryptPbkdf2(String ciphertext, String password) throws IOException {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }

        byte[] salt = fromBase64(fields[0]);
        byte[] iv = fromBase64(fields[1]);
        byte[] cipherBytes = fromBase64(fields[2]);
        SecretKey key = deriveKeyPbkdf2(salt, password);

        return decrypt(cipherBytes, key, iv);
    }

    public static String decryptNoSalt(String ciphertext, SecretKey key) throws IOException {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 2) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        byte[] iv = fromBase64(fields[0]);
        byte[] cipherBytes = fromBase64(fields[1]);

        return decrypt(cipherBytes, key, iv);
    }
    public static byte[] decryptNoSaltToBytes(String ciphertext, SecretKey key) throws IOException {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 2) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        byte[] iv = fromBase64(fields[0]);
        byte[] cipherBytes = fromBase64(fields[1]);

        return decryptToBytes(cipherBytes, key, iv);
    }
}
