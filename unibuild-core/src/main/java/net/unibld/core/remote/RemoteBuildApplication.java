package net.unibld.core.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Standalone application to run the remote build client.
 *
 */
public class RemoteBuildApplication 
{
    private static final String EXECUTABLE = "remote-build";

	public static void main( String[] args )
    {
    	Map<String,OptArg> optArgs=new HashMap<String,OptArg>();
    	List<String> defaultArgs=new ArrayList<String>();
    	for (int i=0;i<args.length;i++) {
    		String arg=args[i].trim().replace("\"", "");
    		if (arg.startsWith("--")) {
    			//options
    			OptArg o = OptArg.parse(arg);
				optArgs.put(o.key,o);
    		} else if (arg.equals(EXECUTABLE)) {
    			
    		} else {
    			defaultArgs.add(arg);
    		}
    	}
    	
    	
        if (defaultArgs.size()<3) {
        	System.out.println(usage());
        	System.exit(1);
        	return;
        }
        
        String host=defaultArgs.get(0);
        String ticket=defaultArgs.get(1);
        String project=defaultArgs.get(2);
        
        
        RemoteBuildClient cl=new RemoteBuildClient(host,ticket, project);
        cl.initialize(optArgs);
        cl.executeBuild();
    }

	private static String usage() {
		StringBuilder b=new StringBuilder();
		b.append("Usage: remote-build [OPTIONS] HOST PROJECT [BUILD_DIR]\n");
		return b.toString();
	}
}
