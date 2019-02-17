package net.unibld.server.util;

public class UrlConcat {
	/**
	 * Concatenates several URL parts.
	 * @param urlParts Url parts in var args
	 * @return Concatenated full URL
	 */
	public static String concatenateUrlParts(String ... urlParts) {
		if (urlParts==null||urlParts.length==0) {
			return null;
		}
		StringBuffer b=new StringBuffer();
		int cycleStart=0;
		if (urlParts[0].contains("://")) {
			b.append(urlParts[0]);
			cycleStart=1;
		}
		
		for (int i=cycleStart;i<urlParts.length;i++) {
			if (b.toString().endsWith("/")) {
				if (urlParts[i].startsWith("/")) {
					b.append(urlParts[i].substring(1));
				} else {
					b.append(urlParts[i]);
				}
			} else {
				if (urlParts[i].startsWith("/")) {
					b.append(urlParts[i]);
				} else {
					b.append('/');
					b.append(urlParts[i]);
				}
			}
		}
		return b.toString();
	}
	
}
