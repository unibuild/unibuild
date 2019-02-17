package net.unibld.core.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import net.unibld.core.Parameter;
import net.unibld.core.build.ParameterMap;
import net.unibld.core.log.LoggerHelper;
import net.unibld.core.log.LoggingScheme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoggingConfiguration {
	private static final Logger LOGGER=LoggerFactory.getLogger(LoggingConfiguration.class);
	
	@Value("${logging.logger:net.unibld.core.log.SimpleConsoleLogger}")
	private String defaultLoggerClass;
	
	private String defaultLoggerFormat="${task} ${level} ${msg}";
	
	@Autowired
	private LoggingScheme loggingScheme;
	
	public void init(ParameterMap parameters) {
		init(parameters.isVerbose(),parameters.isTrace());
	}

	public void init(boolean verbose,boolean trace) {
		loggingScheme.clearLoggers();
		LoggerConfig cfg = new LoggerConfig();
		cfg.setLoggerClass(defaultLoggerClass);
		cfg.setParameters(new ArrayList<Parameter>());
		Parameter p = new Parameter();
		p.setName("format");
		p.setValue(defaultLoggerFormat);
		cfg.getParameters().add(p);
		loggingScheme.addLogger(LoggerHelper.createLogger( cfg,verbose,trace));
		LOGGER.info("Logging configuration inited with default logger: {}",defaultLoggerClass);
	}
	
}
