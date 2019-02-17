package net.unibld.core.log;

import net.unibld.core.BuildProject;

public class WelcomeMessageHelper {

	public static String getWelcomeMessage(BuildProject project,String goal,String logFile) {
		//echo    "=================================================================" >&3
		//echo    "[ProjectName] BUILD SESSION" >&3
		//echo    "Log file: ${BUILD_LOG}" >&3
		//echo    "=================================================================" >&3
		StringBuilder b=new StringBuilder();
		b.append("=================================================================\n");
		b.append(String.format("%s: %s\n",project.getName().toUpperCase(), 
				goal.toUpperCase()));
		if (logFile!=null) {
			b.append(String.format("Log file: %s\n",logFile));
			
		}
		b.append("=================================================================\n");
		return b.toString();
	}

}
