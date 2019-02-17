package net.unibld.core.remote;

public class OptArg {
	public String key;
	public String value;
	
	public static OptArg parse(String arg) {
		if (!arg.startsWith("--")) {
			throw new IllegalArgumentException("Invalid argument: "+arg);
		}
		arg=arg.replace("--", "");
		String[] split=arg.split("=");
		if (split.length==1) {
			OptArg ret=new OptArg();
			ret.key=split[0];
			return ret;
		} else if (split.length==2) {
			OptArg ret=new OptArg();
			ret.key=split[0];
			ret.value=split[1];
			return ret;
		} else {
			throw new IllegalArgumentException("Invalid argument: "+arg);
		}
	}
}
