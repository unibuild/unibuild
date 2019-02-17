package net.unibld.core.task.impl.java;

import net.unibld.core.security.BuildCredential;
import net.unibld.core.security.ICredentialStore;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.AbstractExecTaskRunner;
import net.unibld.core.util.PlatformHelper;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

/**
 * Base class for JAR-signer task runners.
 * @author andor
 *
 * @param <T> Generic task parameter
 */
public abstract class AbstractSignJarTaskRunner<T extends SignJarTask> extends AbstractExecTaskRunner<T> {

	
	protected String getExecutableName() {
		return PlatformHelper.isWindows()?"jarsigner.exe":"jarsigner";
		
	}
	
	
	
	@Override
	protected String getArguments(T task) {
		if (task.getSigAlg()==null) {
			LoggerFactory.getLogger(getClass()).error("Sig alg not specified");
			throw new IllegalArgumentException("Signature algorythm not specified");
		}
		if (task.getDigestAlg()==null) {
			LoggerFactory.getLogger(getClass()).error("Digest alg not specified");
			throw new IllegalArgumentException("Digest algorythm not specified");
		}
		//if (getKeystore()==null) {
		//	throw new IllegalArgumentException("Keystore not specified");
		//}
		if (task.getInput()==null) {
			LoggerFactory.getLogger(getClass()).error("Input not specified");
			throw new IllegalArgumentException("Input not specified");
		}
		
		StringBuilder b=new StringBuilder();
		
		addVmArgs(b);
		//jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.keystore
		//		my_application.apk alias_name
		if (task.isVerbose()) {
			b.append("-verbose ");
		}
		b.append(" -sigalg ");
		b.append(task.getSigAlg());
		b.append(" -digestalg ");
		b.append(task.getDigestAlg());
		b.append(" -keystore ");
		b.append(getKeystore(task));
		
		
		String stpw=null;
		
		if (task.isStorePasswordProtected()||task.isKeyPasswordProtected()) {
			stpw=getStorePassword(task);
			
		}
		if (task.isStorePasswordProtected()) {
			if (stpw!=null) {
				b.append(" -storepass ");
				b.append(stpw);
			}
		}
		if (task.isKeyPasswordProtected()) {
			String kpw=getKeyPassword(task);
			if (kpw!=null) {
				b.append(" -keypass ");
				b.append(kpw);
			} 
		}
		if (task.getOutput()!=null) {
			b.append(" -signedjar ");
			b.append(task.getOutput());
		
		}
		b.append(" ");
		b.append(task.getInput());
		if (isAliasSpecified(task)) {
			b.append(" ");
			b.append(getAlias(task));	
		}
		LoggerFactory.getLogger(getClass()).info("Jarsigner args: {}",b.toString());
		return b.toString();
	}


	protected String getAlias(T task) {
		return task.getAlias();
	}


	protected boolean isAliasSpecified(T task) {
		return getAlias(task)!=null;
	}


	protected String getKeystore(T task) {
		return task.getKeystore();
	}


	protected String getStorePassword(T task) {
		
		
		ICredentialStore store = credentialStoreFactory.getStore(task.getPasswordStrategy());
		LoggerFactory.getLogger(getClass()).info("Checking for jarsigner store auth in credential store {} with strategy: {}...",store.getClass().getName(),task.getPasswordStrategy().name());
		
		BuildCredential cred = store.getPassword("jarsigner-store", getKeystore(task), getAlias(task));
		if (cred!=null) {
			return cred.getPassword();
		}
		return null;
	}


	
	protected String getKeyPassword(T task) {
		
		
		
		ICredentialStore store = credentialStoreFactory.getStore(task.getPasswordStrategy());
		LoggerFactory.getLogger(getClass()).info("Checking for jarsigner key auth in credential store {} with strategy: {}...",store.getClass().getName(),task.getPasswordStrategy().name());
		
		BuildCredential cred =store.getPassword("jarsigner", task.getInput(), getAlias(task));
		if (cred!=null) {
			return cred.getPassword();
		}
		return null;
	}


	protected void addVmArgs(StringBuilder b) {
		// do nothing
		
	}



	@Override
	protected ExecutionResult execute(T task) {
		
		String exe = getExecutableFile(task);
		logTask("Signing jar %s with jarsigner...", task.getInput());
		task.setPath(exe);
		return execCmd(task);
	}


	protected String getExecutableFile(T task) {
		if (task.getJdkDir()==null) {
			LoggerFactory.getLogger(getClass()).info("Using default JVM...");
			return getExecutableName();
		}
		LoggerFactory.getLogger(getClass()).info("Using JDK dir: {}...",task.getJdkDir());
		String ret = FilenameUtils.concat(FilenameUtils.concat(task.getJdkDir(), "bin"),getExecutableName());
		if (ret.contains(" ")) {
			ret=String.format("\"%s\"",ret);
		}
		return ret;
	}

}
