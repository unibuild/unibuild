package net.unibld.core.var.el;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import net.unibld.core.build.BuildException;
import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.var.IVariableSupportProvider;
import net.unibld.core.var.RegexVarHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A variable provider that uses EL to evaluate properties. By default, the EL implementation used is
 * Jasper EL by Apache for Tomcat/JSP.
 * @author andor
 *
 */
@Component
public class ELVariableProvider implements IVariableSupportProvider {
	private static final Logger LOG = LoggerFactory
			.getLogger(ELVariableProvider.class);

	private static final String EL_FACTORY_APACHE = "org.apache.el.ExpressionFactoryImpl";

	private ExpressionFactory expressionFactory;
	//private Map<String, String> transformedNames;
	


	public String substitute(String str,IBuildContextAttributeContainer container) {
		if (str == null) {
			return null;
		}

		// load the expression factory
		if (expressionFactory == null) {
			ClassLoader cl = this.getClass().getClassLoader();
			try {
				Class<?> expressionFactoryClass = cl
						.loadClass(EL_FACTORY_APACHE);
				expressionFactory = (ExpressionFactory) expressionFactoryClass
						.newInstance();
				LOG.info("javax.el.ExpressionFactory=" + EL_FACTORY_APACHE);
			} catch (Exception ex) {
				LOG.error("Failed to create expression factory: "
						+ EL_FACTORY_APACHE, ex);
				throw new BuildException(
						"Failed to create expression factory: "
								+ EL_FACTORY_APACHE, ex);
			}
		}
		// create a map with some variables in it
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		Map<String, String> transformedNames = fillVariableMap(userMap,container);

		str = replaceTransformedNames(transformedNames, str);
		
		Matcher m = RegexVarHelper.getMatcher(str);
		while(m.find()) {
			String var = m.group(1);
			if (var.contains(".")) {
				throw new BuildException("Undefined variable: "+var+" (variable containing an illegal '.')");
			}
		}
		// get the method for ${myprefix:hello(string)}
		// Method sayHello = DemoEL.class.getMethod("sayHello", new
		// Class[]{String.class});

		// create the context
		ELResolver demoELResolver = new ELResolverImpl(userMap);
		final VariableMapper variableMapper = new VariableMapperImpl();
		final FunctionMapperImpl functionMapper = new FunctionMapperImpl();
		// functionMapper.addFunction("myprefix", "hello", sayHello);
		final CompositeELResolver compositeELResolver = new CompositeELResolver();
		compositeELResolver.add(demoELResolver);
		compositeELResolver.add(new ArrayELResolver());
		compositeELResolver.add(new ListELResolver());
		compositeELResolver.add(new BeanELResolver());
		compositeELResolver.add(new MapELResolver());
		ELContext context = new ELContext() {
			@Override
			public ELResolver getELResolver() {
				return compositeELResolver;
			}

			@Override
			public FunctionMapper getFunctionMapper() {
				return functionMapper;
			}

			@Override
			public VariableMapper getVariableMapper() {
				return variableMapper;
			}
		};

		// create and resolve a value expression
		// String sumExpr = "${x+y}";
		ValueExpression ve = expressionFactory.createValueExpression(context,
				str, Object.class);
		Object result = ve.getValue(context);
		LOG.debug("EL evaluate result=" + result);

		String ret = result == null ? null : result.toString();
		if (ret != null) {
			checkUndefinedVariable(ret);
		}
		return ret;
	}

	protected void checkUndefinedVariable(String ret) {
		Matcher m = RegexVarHelper.getMatcher(ret);
		if(m.find()) {
            String var = m.group(1);
            throw new BuildException("Undefined variable: "+var);
		}

	}

	private String replaceTransformedNames(Map<String, String> transformedNames, String str) {
		if (str == null) {
			return null;
		}

		if (transformedNames.size() == 0) {
			return str;
		}

		if (!str.contains("${")) {
			return str;
		}

		for (String key : transformedNames.keySet()) {
			str = str.replace(key, transformedNames.get(key));
		}
		return str;
	}

	private Map<String, String> fillVariableMap(Map<Object, Object> userMap, IBuildContextAttributeContainer container) {
		Map<String, String> transformedNames = new HashMap<String, String>();
		Map<String, String> map = container.getTaskContextAttributeMap();
		for (String key : map.keySet()) {
			String val = map.get(key);
			if (needsEscape(key)) {
				String keyEsc = escapeKey(key);
				userMap.put(keyEsc, val);
				transformedNames.put(key, keyEsc);
				LOG.debug("Using transformed var '{}' (original: '{}', container: '{}'): {}",key, keyEsc,container.getClass().getName(),val);
			} else {
				userMap.put(key, val);
				LOG.debug("Using simple var '{}' in container {}: {}",key, val,container.getClass().getName());
			}
		}
		return transformedNames;
	}

	private String escapeKey(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Key was null");
		}
		return key.replace(".", "_");
	}

	private boolean needsEscape(String key) {
		if (key == null || key.trim().length() == 0) {
			return false;
		}
		return key.contains(".");
	}

}
