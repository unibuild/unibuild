package net.unibld.core.task.impl.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.ExecutionResult;


/**
 * A task that uploads/downloads a file to or from a host, using the SCP protocol over SSH2.
 * @author andor
 *
 */
@Component
public class ScpTaskRunner extends AbstractSshTaskRunner<ScpTask> {
	private static final Logger LOG=LoggerFactory.getLogger(ScpTaskRunner.class);
	
	
	protected ExecutionResult execute(ScpTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		
		if (task.getFile()==null&&task.getRemoteFile()==null) {
			throw new BuildException("Neither local or remote file was specified");
		}
		
		if (task.getFile()!=null) {
			if (task.getRemoteDir()==null) {
				throw new BuildException("Remote dir was not specified");
			}
			logTask("Uploading {0} to: {1}@{2}:{3} using SCP...", task.getFile(),task.getRemoteDir(),task.getHost(),String.valueOf(task.getPort()));
			
			
			File file=new File(task.getFile());
			if (!file.exists()) {
				throw new BuildException("File does not exist: "+file.getAbsolutePath());
			}
			
			Session session=null;
			try {
				session = createSshSession(task);
				sendFile(session, file,task.getRemoteDir(),task.getRemoteFileName());
				
			} catch (Exception ex) {
				LOG.error("Failed to SCP to: "+task.getHost());
				throw new BuildException("Failed to SCP to: "+task.getHost(),ex);
			} finally {
				if (session!=null) {
					session.disconnect();
				}
			}
			
			return ExecutionResult.buildSuccess();
		} else {
			if (task.getLocalDir()==null) {
				throw new BuildException("Local dir was not specified");
			}
			
			logTask("Downloading {0}@{1}:{2} to: {3} using SCP...", task.getRemoteFile(),task.getHost(),String.valueOf(task.getPort()),
					task.getLocalDir());
			
			File localDir=new File(task.getLocalDir());
			localDir.mkdirs();
			
			Session session=null;
			try {
				session = createSshSession(task);
				receiveFile(session, localDir, task.getRemoteFile());
			} catch (Exception ex) {
				LOG.error("Failed to SCP to: "+task.getHost());
				throw new BuildException("Failed to SCP to: "+task.getHost(),ex);
			} finally {
				if (session!=null) {
					session.disconnect();
				}
			}
			return ExecutionResult.buildSuccess();
		}
	}
	
	static int checkAck(InputStream in) throws IOException{
	    int b=in.read();
	    // b may be 0 for success,
	    //          1 for error,
	    //          2 for fatal error,
	    //          -1
	    if(b==0) return b;
	    if(b==-1) return b;

	    if(b==1 || b==2){
	      StringBuffer sb=new StringBuffer();
	      int c;
	      do {
		c=in.read();
		sb.append((char)c);
	      }
	      while(c!='\n');
	      if(b==1){ // error
	    	  throw new IllegalStateException(sb.toString());
	      }
	      if(b==2){ // fatal error
	    	  throw new IllegalStateException(sb.toString());
	      }
	    }
	    return b;
	  }


	private void receiveFile(Session session, File localDir,String rfile) throws JSchException, IOException {
		FileOutputStream fos = null;
		try {
			// exec 'scp -f rfile' remotely
			String command = "scp -f " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}

				// System.out.println("filesize="+filesize+", file="+file);

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				fos = new FileOutputStream(FilenameUtils.concat(localDir.getAbsolutePath(),
						file));
				int foo;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}
				fos.close();
				fos = null;

				if (checkAck(in) != 0) {
					throw new IllegalStateException("Failed to receive file");
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}

			session.disconnect();

			
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ee) {
				LOG.error("Failed to close output stream",ee);
			}
		}
	}
	  

	private void sendFile(Session session, File file, String remoteDir,String remoteFileName) throws JSchException, IOException {
		
		boolean ptimestamp = true;
		
		String rfile=remoteDir+"/"+(remoteFileName!=null?remoteFileName:file.getName());

		// exec 'scp -t rfile' remotely
		String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
		Channel channel = session.openChannel("exec");
		
		try {
			((ChannelExec) channel).setCommand(command);
	
			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
	
			channel.connect();
	
			if (checkAck(in) != 0) {
				throw new IllegalStateException("Could not connect to channel");
			}
	
		
			if (ptimestamp) {
				command = "T" + (file.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (file.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					throw new IllegalStateException("Access time could not be sent");
				}
			}
	
			// send "C0644 filesize filename", where filename should not include '/'
			long filesize = file.length();
			command = "C0644 " + filesize + " ";
			/*if (lfile.lastIndexOf('/') > 0) {
				command += lfile.substring(lfile.lastIndexOf('/') + 1);
			} else {
				command += lfile;
			}*/
			//command+=(remoteFileName!=null?remoteFileName:file.getName());
			command+=file.getName();
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				throw new IllegalStateException("File name and size could not be sent");
			}
	
			// send a content of lfile
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				throw new IllegalStateException("File could not be sent");
			}
			out.close();
		} finally {

			channel.disconnect();
		}
	}


	


	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "mkdir";
	}
}//end CustomNativeTaskRunner