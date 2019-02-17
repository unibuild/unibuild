package net.unibld.core.scm;

public class RealmHelper {

	public static String createRealm(String url) {
		if (url==null) {
			throw new IllegalArgumentException("URL was null");
		}
		int start=0;
		boolean https=false;
		if (url.startsWith("http://")) {
			start="http://".length()+1;
		} else if (url.startsWith("https://")) {
			start="https://".length()+1;
			https=true;
		} else {
			throw new IllegalArgumentException("Invalid protocol in URL: "+url);
		}
		
		int next=url.indexOf("/",start);
		String ret=null;
		if (next!=-1) {
			ret=url.substring(0,next);
		} else {
			ret=url;
		}
		int portStart=url.indexOf(":",start);
		if (portStart==-1) {
			ret+=https?":443":":80";
		}
		return ret;
	}

	
}
