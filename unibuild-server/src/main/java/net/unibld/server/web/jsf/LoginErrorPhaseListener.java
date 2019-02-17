package net.unibld.server.web.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.naming.AuthenticationException;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;

public class LoginErrorPhaseListener implements PhaseListener {
	private static final long serialVersionUID = -1216620620302322995L;

	@Override
	public void beforePhase(final PhaseEvent arg0) {
		Exception e = (Exception) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap()
				.get(WebAttributes.AUTHENTICATION_EXCEPTION);

		if (e instanceof BadCredentialsException||e instanceof AuthenticationException||e instanceof AuthenticationServiceException) {
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.getSessionMap()
					.put(WebAttributes.AUTHENTICATION_EXCEPTION, null);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Invalid user name or password",
							null));
		}
	}

	@Override
	public void afterPhase(final PhaseEvent arg0) {
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}
