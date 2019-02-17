package net.unibld.core.task.impl.java;

import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ExecTask;

/**
 * A task that is capable of signing jars with a keystore file, using algorythms supported by the selected JDK (via the jdkDir parameter,
 * if that is omitted, the task tries to use the default JVM that is supposed to be a JDK).
 * @author andor
 *
 */
@Task(name="signjar",runnerClass=SignJarTaskRunner.class)
public class SignJarTask extends ExecTask {
	private String input;
	private String output;
	private String sigAlg;
	private String digestAlg;
	
	private String alias;
	private boolean storePasswordProtected;
	private boolean keyPasswordProtected;
	
	private String keystore;
	private boolean verify;
	private boolean verbose;
	private boolean verifyCerts;
	private String jdkDir;
	
	private PasswordStrategy passwordStrategy=PasswordStrategy.stored;

	
	/**
	 * @return Input jar to sign
	 */
	public String getInput() {
		return input;
	}
	/**
	 * @param input Input jar to sign
	 */
	public void setInput(String input) {
		this.input = input;
	}
	/**
	 * @return Signed output jar
	 */
	public String getOutput() {
		return output;
	}
	/**
	 * @param output Signed output jar
	 */
	public void setOutput(String output) {
		this.output = output;
	}
	/**
	 * @return Signing algorythm (must be supported in the JVM used)
	 */
	public String getSigAlg() {
		return sigAlg;
	}
	/**
	 * @param sigAlg Signing algorythm (must be supported in the JVM used)
	 */
	public void setSigAlg(String sigAlg) {
		this.sigAlg = sigAlg;
	}
	/**
	 * @return Digest algorythm (must be supported in the JVM used)
	 */
	public String getDigestAlg() {
		return digestAlg;
	}
	/**
	 * @param digestAlg Digest algorythm (must be supported in the JVM used)
	 */
	public void setDigestAlg(String digestAlg) {
		this.digestAlg = digestAlg;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getKeystore() {
		return keystore;
	}
	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}
	public boolean isVerify() {
		return verify;
	}
	public void setVerify(boolean verify) {
		this.verify = verify;
	}
	/**
	 * @return True if verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}
	/**
	 * @param verbose True if verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public PasswordStrategy getPasswordStrategy() {
		return passwordStrategy;
	}
	public void setPasswordStrategy(PasswordStrategy passwordStrategy) {
		this.passwordStrategy = passwordStrategy;
	}
	public boolean isStorePasswordProtected() {
		return storePasswordProtected;
	}
	public void setStorePasswordProtected(boolean storePasswordProtected) {
		this.storePasswordProtected = storePasswordProtected;
	}
	public boolean isKeyPasswordProtected() {
		return keyPasswordProtected;
	}
	public void setKeyPasswordProtected(boolean keyPasswordProtected) {
		this.keyPasswordProtected = keyPasswordProtected;
	}
	public boolean isVerifyCerts() {
		return verifyCerts;
	}
	public void setVerifyCerts(boolean verifyCerts) {
		this.verifyCerts = verifyCerts;
	}
	/**
	 * @return JDK path for jarsigner, configured in the task (overriding the default JVM being used)
	 */
	public String getJdkDir() {
		return jdkDir;
	}
	/**
	 * @param jdkDir JDK path for jarsigner, configured in the task (overriding the default JVM being used)
	 */
	public void setJdkDir(String jdkDir) {
		this.jdkDir = jdkDir;
	}

	
}
