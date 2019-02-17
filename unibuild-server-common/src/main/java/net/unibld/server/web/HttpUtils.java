package net.unibld.server.web;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	private static final Logger LOG=LoggerFactory.getLogger(HttpUtils.class);
	
	public static void traceRequest(HttpServletRequest req) {
		traceRequest(req,LOG,false);
	}
	@SuppressWarnings("rawtypes")
	public static void traceRequest(HttpServletRequest req,Logger logger,boolean traceAttributes) {
		Enumeration names = req.getParameterNames();
		logger.info("Tracing request parameters: {}...",req.getRequestURI());
		while (names.hasMoreElements()) {
			String name=(String) names.nextElement();
			logger.info("Request parameter '{}': {}",name,req.getParameter(name));
		}
		
		names = req.getAttributeNames();
		logger.info("Tracing request attributes: {}...",req.getRequestURI());
		while (names.hasMoreElements()) {
			String name=(String) names.nextElement();
			logger.info("Request attribute '{}': {}",name,req.getAttribute(name));
		}
		
		names = req.getHeaderNames();
		logger.info("Tracing request header: {}...",req.getRequestURI());
		while (names.hasMoreElements()) {
			String name=(String) names.nextElement();
			logger.info("Request header '{}': {}",name,req.getHeader(name));
		}
		
		
	}
	public static void traceRequest(HttpServletRequest req, Logger logger) {
		traceRequest(req, logger, false);
	}

	public static String getBaseUrl(HttpServletRequest req) {
		StringBuffer url = req.getRequestURL();
		String uri = req.getRequestURI();
		String ctx = req.getContextPath();
		String base = url.substring(0, url.length() - uri.length() + ctx.length()) + "/";
		LOG.info("Base URL detected: {}",base);
		return base;
	}
}
