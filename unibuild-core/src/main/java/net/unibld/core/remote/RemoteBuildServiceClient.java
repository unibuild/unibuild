package net.unibld.core.remote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client for a remote build service on another UniBuild server instance.<br>
 * The client uses a HTTP/REST interface, using Apache HttpClient 4.5.<br>
 * Authentication is done using a ticketing system.
 * @author andor
 *
 */
@SuppressWarnings("deprecation")
public class RemoteBuildServiceClient {
	private static final Logger LOGGER=LoggerFactory.getLogger(RemoteBuildServiceClient.class);
	
	private HttpClient httpClient;
	private String baseUrl;
	
	/**
	 * Constructor with a server URL that also constructs a HTTP client
	 * @param serverUrl Server URL
	 */
	public RemoteBuildServiceClient(String serverUrl) {
		this.baseUrl=getServiceUrl(serverUrl);
		this.httpClient=new DefaultHttpClient();
	}
	
	
	private static String getServiceUrl(String serverUrl) {
		if (serverUrl.endsWith("/")) {
			return String.format("%srest/remote",serverUrl);
		}
		return String.format("%s/rest/remote",serverUrl);
	}


	/**
	 * Starts a remote build process by uploading a zipped resource file to the remote UniBuild server
	 * @param ticket Authentication ticket
	 * @param projectPath Path of the project file on the remote server
	 * @param goal Goal to execute on the remote server (or null if 'build' should be executed)
	 * @param zipFile Resource zip file to upload
	 * @return Response DTO from the remote server
	 * @throws Exception If an error occurs.
	 */
	public BuildStartedDto uploadZip(String ticket,String projectPath,String goal,File zipFile) throws Exception {
		String url = String.format("%s/upload?ticket=%s&path=%s",baseUrl,ticket,URLEncoder.encode(projectPath,"UTF-8"));
		if (goal!=null) {
			url+=("&goal="+goal);
		}
		
		HttpPost httppost = new HttpPost(url);
		MultipartEntity t = new MultipartEntity();
		t.addPart("file", new FileBody(zipFile));
		//t.addPart(new FormBodyPart("t", new FileBody(photo))); I tired this too
		httppost.setEntity(t);
		HttpResponse response = httpClient.execute(httppost);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body=handler.handleResponse(response);
		
		int code = response.getStatusLine().getStatusCode();
		
		
		if (code!=200) {
			throw new IOException(String.format("HTTP error: code=%d, body=%s", code,body));
		}
		
		Serializer serializer = new Persister();
		return serializer.read(BuildStartedDto.class, body);
	}


	public boolean isBuildRunning(String ticket,String id) throws Exception {
		BuildState state = getBuildState(ticket,id);
		return state.isRunning();
		
	}


	protected BuildState getBuildState(String ticket,String id) throws IOException,
			ClientProtocolException, Exception {
		String url = String.format("%s/state?ticket=%s&id=%s",baseUrl,ticket,id);
		
		
		HttpGet get = new HttpGet(url);
		HttpResponse response = httpClient.execute(get);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body=handler.handleResponse(response);
		
		int code = response.getStatusLine().getStatusCode();
		
		
		if (code!=200) {
			throw new IOException(String.format("HTTP error: code=%d, body=%s", code,body));
		}
		
		Serializer serializer = new Persister();
		return serializer.read(BuildState.class, body);
	}


	public boolean isBuildCompleted(String ticket,String id) throws Exception {
		BuildState state = getBuildState(ticket,id);
		return state.isCompleted();
			
	}

	public String getBuildError(String ticket,String id) throws Exception {
		BuildState state = getBuildState(ticket,id);
		return state.getError();
	
	}


	public String downloadOutput(String ticket, String id,String fileName,String outputPath) throws IOException {
		
			String url = String.format("%s/download?ticket=%s&id=%s",baseUrl,ticket,id);
			if (fileName!=null) {
				url+=("&fileName="+URLEncoder.encode(fileName, "UTF-8"));
			}
			String filePath = FilenameUtils.concat(outputPath, fileName!=null?stripFileName(fileName):"output.zip");
	        
			
			
			
			File destination=new File(filePath);
			if (destination.exists()) {
				try {
					destination.delete();
					LOGGER.info("Deleted destination file: "+
							destination.getAbsolutePath());
				} catch (Exception ex) {
					LOGGER.error("Failed to delete previous audio file", ex);
				}
			}

			HttpGet get = new HttpGet(url);
			HttpResponse response = httpClient.execute(get);
			 HttpEntity entity = response.getEntity();
			 if (entity != null) {
			     InputStream instream = entity.getContent();
			     try {
			         BufferedInputStream bis = new BufferedInputStream(instream);
			          BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
			         int inByte;
			         while ((inByte = bis.read()) != -1 ) {
			             bos.write(inByte);
			         }
			         bis.close();
			         bos.close();
			         return filePath; 
			     } catch (IOException ex) {
			         throw ex;
			     } catch (RuntimeException ex) {
			         get.abort();
			         throw ex;
			     } finally {
			         instream.close();
			     }
			     
			 } else {
				 return null;
			 }
		
	}


	private String stripFileName(String fileName) {
		int slash = fileName.lastIndexOf("/");
		if (slash!=-1) {
			return fileName.substring(slash+1);
		}
		slash = fileName.lastIndexOf("\\");
		if (slash!=-1) {
			return fileName.substring(slash+1);
		}
		return fileName;
	}

}
