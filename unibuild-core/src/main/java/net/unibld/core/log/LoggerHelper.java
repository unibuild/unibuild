package net.unibld.core.log;

import java.util.ArrayList;
import java.util.List;

import net.unibld.core.build.BuildToolContext;
import net.unibld.core.build.ParameterMap;
import net.unibld.core.build.ProjectBuilder;
import net.unibld.core.config.LogConfig;
import net.unibld.core.config.LoggerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerHelper {
	private static final Logger LOG=LoggerFactory.getLogger(ProjectBuilder.class);

	@SuppressWarnings("unchecked")
	public static List<IBuildLogger> createLoggers(LogConfig logConfig,boolean verbose, boolean trace) {
		List<IBuildLogger> ret=new ArrayList<IBuildLogger>();
		if (logConfig!=null) {
			List<LoggerConfig> loggerConfigs = logConfig.getLoggerConfigs();
			for (LoggerConfig lc:loggerConfigs) {
				IBuildLogger logger = createLogger(lc,verbose,trace);
				if (logger!=null) {
					ret.add(logger);
				}
			}
			LOG.info("Logging scheme inited from project confih");
		}
		return ret;
	}

	public static IBuildLogger createLogger(LoggerConfig lc,boolean verbose,boolean trace) {
		try {
			Class<? extends IBuildLogger> lCl = (Class<? extends IBuildLogger>) Class.forName(lc.getLoggerClass());
			IBuildLogger logger = lCl.newInstance();
			logger.configure(lc,verbose,trace);
			return logger;
		} catch (Exception ex) {
			LOG.error("Failed to init logger",ex);
			return null;
		}
	}
}
