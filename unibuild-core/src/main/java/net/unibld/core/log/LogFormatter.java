package net.unibld.core.log;

public class LogFormatter {

	public static String replaceArgs(String msg, String[] args) {
		if (msg==null) {
			throw new IllegalArgumentException("Message was null");
		}
		if (args==null||args.length==0) {
			return msg;
		}
		
		for (int i=0;i<args.length;i++) {
			msg=msg.replace(String.format("{%d}", i),args[i]);
		}
		return msg;
	}

}
