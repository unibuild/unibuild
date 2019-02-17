package net.unibld.core.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.config.StoreConfig;

/**
 * A credential store that uses the secret-tool command from the Gnome libsecret-tools library on Linux
 * @author andor
 *
 */
public class LibSecretCredentialStore extends AbstractCredentialStore {
	private static final Logger LOGGER = LoggerFactory.getLogger(LibSecretCredentialStore.class);
	
	private StoreConfig storeConfig;
	
	/**
	 * Constructor with store config
	 * @param cfg Store config
	 */
	public LibSecretCredentialStore(StoreConfig cfg) {
		this.storeConfig=cfg;
	}
	
	/**
	 * Default constructor
	 */
	public LibSecretCredentialStore() {
		this(null);
	}

	@Override
	public void clearAll() {
		throw new UnsupportedOperationException("Clear all is not supported in libsecret");
	}

	@Override
	protected void putToStore(CredentialKey key, String passStr, String desc) {
		String[] kvp=getKeyValuePairs(key);
		Executor exec = new DefaultExecutor();

	    CommandLine cl = new CommandLine("secret-tool");
	    cl.addArgument("store");
	    cl.addArgument(String.format("--label='%s'",desc),false);
	    cl.addArguments(kvp);

	    ByteArrayInputStream input =
	        new ByteArrayInputStream(passStr.getBytes());
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    ByteArrayOutputStream errOutput = new ByteArrayOutputStream();

	    exec.setStreamHandler(new PumpStreamHandler(output, errOutput, input));
	    try {
			exec.execute(cl);
			LOGGER.info("Password stored for {} at {} ({})",key.getIdentity(),key.getTarget(),key.getType());
		} catch (Exception e) {
			LOGGER.error("Failed to store password using libsecret",e);
			throw new IllegalStateException("Failed to store password using libsecret",e);
		}

	}

	@Override
	protected boolean storeContains(CredentialKey key) {
		return getFromStore(key)!=null;
	}

	@Override
	protected void removeFromStore(CredentialKey key) {
		String[] kvp=getKeyValuePairs(key);
		
		Executor exec = new DefaultExecutor();

	    CommandLine cl = new CommandLine("secret-tool");
	    cl.addArgument("clear");
	    cl.addArguments(kvp);

	    ByteArrayOutputStream output = new ByteArrayOutputStream();

	    exec.setStreamHandler(new PumpStreamHandler(output));
	    try {
			exec.execute(cl);
			LOGGER.info("Password removed for {} at {} ({})",key.getIdentity(),key.getTarget(),key.getType());
		} catch (Exception e) {
			LOGGER.error("Failed to remove password using libsecret",e);
			throw new IllegalStateException("Failed to remove password using libsecret",e);
			
		}

	}

	@Override
	protected String getFromStore(CredentialKey key) {
		String[] kvp=getKeyValuePairs(key);
		
		Executor exec = new DefaultExecutor();

	    CommandLine cl = new CommandLine("secret-tool");
	    cl.addArgument("lookup");
	    cl.addArguments(kvp);

	    ByteArrayOutputStream output = new ByteArrayOutputStream();

	    exec.setStreamHandler(new PumpStreamHandler(output));
	    try {
			exec.execute(cl);
			String pass=output.toString().trim();
			if (pass.length()==0) {
				return null;
			}
			return pass;
		} catch (Exception e) {
			LOGGER.error("Failed to get password using libsecret",e);
			//throw new IllegalStateException("Failed to get password using libsecret",e);
			return null;
		}

	}

	private String[] getKeyValuePairs(CredentialKey key) {
		return new String[] {"type", key.getType(), 
				"target", key.getTarget(), 
				"identity", key.getIdentity()};
	}
	
}
