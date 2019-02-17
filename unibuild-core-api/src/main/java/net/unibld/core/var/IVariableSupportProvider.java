package net.unibld.core.var;

import net.unibld.core.build.IBuildContextAttributeContainer;

/**
 * An interface for providing variables support to a build context.
 * @author andor
 *
 */
public interface IVariableSupportProvider {
	/**
	 * Substitutes values of the variables of the provided container in the specified string  
	 * @param str Expression string
	 * @param container Variable container
	 * @return Substituted value
	 */
	public String substitute(String str,IBuildContextAttributeContainer container);
}
