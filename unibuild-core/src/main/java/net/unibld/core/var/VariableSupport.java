package net.unibld.core.var;

import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.var.el.ELVariableProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A Spring bean that implements the {@link IVariableSupportProvider} interface using
 * {@link ELVariableProvider} and Exression Language to provide variable support.
 * @author andor
 *
 */
@Component
public class VariableSupport implements IVariableSupportProvider {
	private static final Logger LOG=LoggerFactory.getLogger(VariableSupport.class);

	@Autowired
	private ELVariableProvider provider;

	public String substitute(String str,IBuildContextAttributeContainer container) {
		LOG.debug("Substituting variables using {}...",provider.getClass().getName());
		return provider.substitute(str, container);	
	}

}
