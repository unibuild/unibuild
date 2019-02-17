package net.unibld.server.web.jsf.view.build;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.security.IPasswordReaderInterface;
import net.unibld.core.util.AsyncWaiter;

/**
 * A JSF managed bean that implements the {@link IPasswordReaderInterface} interface and shows a PrimeFaces
 * dialog if password input by the user is necessary.
 * @author andor
 *
 */
@ManagedBean(name="passwordReaderBean")
@ViewScoped
public class PasswordReaderBean implements IPasswordReaderInterface {
	private static final Logger LOG=LoggerFactory.getLogger(PasswordReaderBean.class);

	private String password;
	
	private String credentialType;
	private String target;
	private String identity;

	private boolean passwordSpecified;

	private boolean passwordRequested;
	private boolean dialogCloseRequested;

	
	
	@Override
	public String readPassword(String credentialType, String target, String identity) {
		LOG.info("Waiting for password: {}:{} ({})...",credentialType,target, identity);
		this.credentialType=credentialType;
		this.target=target;
		this.identity=identity;
		this.password=null;
		this.passwordSpecified=false;
		this.passwordRequested=true;
		this.dialogCloseRequested=false;
		
		AsyncWaiter<String> waiter=new AsyncWaiter<>(30000);
		String ret = waiter.waitFor(this::getSpecifiedPassword);
		if (ret==null) {
			dialogCloseRequested=true;
		}
		passwordSpecified=false;
		return ret;
	}
	
	private String getSpecifiedPassword() {
		if (!passwordSpecified) {
			return null;
		}
		return password;
	}
	
	/**
	 * Signals that a password has been specified. Should be invoked by the 'Specify' commandButton from
	 * the dialog.
	 */
	public void specifyPassword() {
		this.passwordSpecified=true;
	}

	/**
	 * @return Password bound
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password Password bound
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Credential type
	 */
	public String getCredentialType() {
		return credentialType;
	}

	/**
	 * @param credentialType Credential type
	 */
	public void setCredentialType(String credentialType) {
		this.credentialType = credentialType;
	}

	/**
	 * @return Authentication target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target Authentication target
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return Identity
	 */
	public String getIdentity() {
		return identity;
	}

	/**
	 * @param identity Identity
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}

	/**
	 * @return True if a password has been requested
	 */
	public boolean isPasswordRequested() {
		return passwordRequested;
	}

	/**
	 * @return True if a dialog closure has been requested.
	 */
	public boolean isDialogCloseRequested() {
		return dialogCloseRequested;
	}

	/**
	 * Shows the password dialog and updates necessary components.
	 */
	public void showPasswordDialog() {
		LOG.info("Showing password dialog...");
		passwordRequested=false;
		RequestContext.getCurrentInstance().update("passwordReaderDialogForm:passwordReaderDetail");
		RequestContext.getCurrentInstance().execute("PF('passwordReaderDialog').show();");
		
	}
	/**
	 * Hides the password dialog and updates necessary components.
	 */
	public void hidePasswordDialog() {
		LOG.info("Hiding password dialog...");
		dialogCloseRequested=false;
		RequestContext.getCurrentInstance().update("container");
		RequestContext.getCurrentInstance().execute("PF('passwordReaderDialog').hide();");
	}
	
}
