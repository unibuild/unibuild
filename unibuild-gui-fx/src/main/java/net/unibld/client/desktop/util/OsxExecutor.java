package net.unibld.client.desktop.util;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes an OS command on OSX
 */
public class OsxExecutor {
	private static final Logger LOGGER=LoggerFactory.getLogger(OsxExecutor.class);
	
	
	private class StreamReader implements Runnable {
		private InputStream stream;
		private String string;

		StreamReader(InputStream is) {
			this.stream=is;
		}

		@Override
		public void run() {
			try {
				byte[] bytes=IOUtils.toByteArray(stream);
				this.string=new String(bytes,"UTF-8");
			} catch (IOException ex) {
				LOGGER.error("Failed to read from stream",ex);
			}
		}

		public void start() {
			Thread thr=new Thread(this);
			thr.start();
		}

		public String getString() {
			return string;
		}
	}

    private String error = null;
    private String output = null;

    /**
     * Privileged script template format string.
     * Format Arguments:
     * <ul>
     * <li> 0 = command
     * <li> 1 = optional with clause
     * </ul>
     */
    private static final String APPLESCRIPT_TEMPLATE = 
        "osascript -e ''try''"
        + " -e ''do shell script \"{0}\" {1}''" 
        + " -e ''return \"Success\"''" 
        + " -e ''on error the error_message number the error_number'' "
        + " -e ''return \"Error: \" & error_message''"
        + " -e ''end try'';";


    /**
     * Executes an OS command
     * @param command OS command to execute
     * @param withPriviledge True if in privileged mode
     */
    public void executeCommand(String command, boolean withPriviledge) {
        String script = MessageFormat.format(APPLESCRIPT_TEMPLATE,
                                             command,
                                             withPriviledge
                                              ?  "with administrator privileges"
                                               : "");
        File scriptFile = null;
        try {
            scriptFile = createTmpScript(script);
            
            // run script
            Process p = Runtime.getRuntime().exec(scriptFile.getAbsolutePath());
            
            StreamReader outputReader = new StreamReader(p.getInputStream());
            outputReader.start();
            StreamReader errorReader = new StreamReader(p.getErrorStream());
            errorReader.start();

            int result = p.waitFor();

            this.output = outputReader.getString();
            if (result != 0) {
                this.error = "Unable to run script " 
                    + (withPriviledge ? "with administrator privileges" : "") 
                    + "\n" + script + "\n"
                        + "Failed with exit code: " + result
                        + "\nError output: " + errorReader.getString();
                return;
            }
        } catch (Exception e) {
            this.error = "Unable to run script:\n" + script
                    + "\nScript execution "
                    + (withPriviledge ? " with administrator privileges" : "") 
                    + " failed: " + e.getMessage();
        } 
    }


	private File createTmpScript(String script) throws IOException {
		String dirPath = System.getProperty("java.io.tmpdir");
		File dir=new File(dirPath);
		dir.mkdirs();
		
		File ret=new File(dir.getAbsolutePath()+"/kinepict-setup");
		FileUtils.write(ret, script, "UTF-8");
		
		Runtime.getRuntime().exec("chmod u+x "+ret.getAbsolutePath());
		return ret;
	}


	/**
	 * @return Error if any
	 */
	public String getError() {
		return error;
	}


	/**
	 * @return Command output
	 */
	public String getOutput() {
		return output;
	}
}