package net.unibld.core.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class LogbackLocator {
	public static void configure(InputStream configStream) throws JoranException, IOException {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.reset();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(loggerContext);
		configurator.doConfigure(configStream); // loads logback file
		configStream.close();
	}
	
	public static InputStream locateLogbackConfig(boolean trace,boolean verbose) throws FileNotFoundException {
		File file=new File("./logback.xml");
		if (file.exists()&&file.isFile()) {
			System.out.println(String.format("Located logback.xml: %s",file.getAbsolutePath()));
			return new FileInputStream(file);
		}
		
		file=new File("./conf/logback.xml");
		
		if (file.exists()&&file.isFile()) {
			if (verbose) {
				System.out.println(String.format("Located logback.xml: %s",file.getAbsolutePath()));
			}
			return new FileInputStream(file);
		}
		
		file=new File("../logback.xml");
		
		if (file.exists()&&file.isFile()) {
			if (verbose) {
				System.out.println(String.format("Located logback.properties: %s",file.getAbsolutePath()));
			}
			return new FileInputStream(file);
		}
		
		file=new File("../conf/logback.xml");
		
		if (file.exists()&&file.isFile()) {
			if (verbose) {
				System.out.println(String.format("Located logback.xml: %s",file.getAbsolutePath()));
			}
			return new FileInputStream(file);
		}
		
		if (trace) {
			InputStream is = Log4JLocator.class.getResourceAsStream(verbose?"/logback-trace-verbose.xml":"/logback-trace.xml");
			if (is!=null) {
				return is;
				
			}	
		}
		
		InputStream is = Log4JLocator.class.getResourceAsStream(verbose?"/logback-verbose.xml":"/logback.xml");
		if (is!=null) {
			if (verbose) {
				System.out.println("Located logback-verbose.xml in classpath");
			}
			return is;
			
		}
		
		throw new IllegalStateException("Could not find logback.xml");
	}
}
