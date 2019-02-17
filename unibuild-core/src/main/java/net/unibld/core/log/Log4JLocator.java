package net.unibld.core.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Log4JLocator {
	public static InputStream locateLog4jConfig(boolean trace,boolean verbose) throws FileNotFoundException {
		File file=new File("./log4j.properties");
		if (file.exists()&&file.isFile()) {
			System.out.println(String.format("Located log4j.properties: %s",file.getAbsolutePath()));
			return new FileInputStream(file);
		}
		
		file=new File("./conf/log4j.properties");
		
		if (file.exists()&&file.isFile()) {
			if (verbose) {
				System.out.println(String.format("Located log4j.properties: %s",file.getAbsolutePath()));
			}
			return new FileInputStream(file);
		}
		
		file=new File("../log4j.properties");
		
		if (file.exists()&&file.isFile()) {
			if (verbose) {
				System.out.println(String.format("Located log4j.properties: %s",file.getAbsolutePath()));
			}
			return new FileInputStream(file);
		}
		
		file=new File("../conf/log4j.properties");
		
		if (file.exists()&&file.isFile()) {
			if (verbose) {
				System.out.println(String.format("Located log4j.properties: %s",file.getAbsolutePath()));
			}
			return new FileInputStream(file);
		}
		
		if (trace) {
			InputStream is = Log4JLocator.class.getResourceAsStream("/log4j-trace.properties");
			if (is!=null) {
				return is;
				
			}	
		}
		
		InputStream is = Log4JLocator.class.getResourceAsStream(verbose?"/log4j-verbose.properties":"/log4j.properties");
		if (is!=null) {
			if (verbose) {
				System.out.println("Located log4j-verbose.properties in classpath");
			}
			return is;
			
		}
		
		throw new IllegalStateException("Could not find log4j.properties");
	}
}
