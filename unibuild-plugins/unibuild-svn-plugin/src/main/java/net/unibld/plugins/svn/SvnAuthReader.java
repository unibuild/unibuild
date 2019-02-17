package net.unibld.plugins.svn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unibld.core.scm.IAuthReader;
import net.unibld.core.scm.RealmHelper;
import net.unibld.core.scm.StoredAuth;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads the built-in Subversion auth file either a specified folder or under  
 * the .subversion/auth/svn-simple folder in the user home
 * @author andor
 *
 */
public class SvnAuthReader implements IAuthReader {
	private static final Logger LOG=LoggerFactory.getLogger(SvnAuthReader.class);
	
	private String externalDir;
	public StoredAuth readStoredAuth(String url,String user) {
		if (url==null) {
			throw new IllegalArgumentException("URL was null");
		}
		String realm=RealmHelper.createRealm(url);
		
		String userHome = System.getProperty("user.home");
		
		File dir=(externalDir==null)?new File(FilenameUtils.concat(userHome,".subversion/auth/svn.simple")):new File(externalDir);
		if (!dir.exists()||!dir.isDirectory()) {
			return null;
		}
		
		File[] files = dir.listFiles();
		for (File f :files) {
			StoredAuth auth=parseFile(f);
			if (auth!=null&&auth.matchUser(user)&&auth.matchRealm(realm)) {
				LOG.info("Stored SVN auth found for {} in realm: {}",auth.getUserName(),realm);
				return auth;
			}
		}
		return null;
	}

	/**
	 * Reads a specific SVN auth file
	 * @param f Auth file
	 * @return Stored auth credential values if the file is a valid SVN auth file
	 */
	public StoredAuth parseFile(File f) {
		try {
			Map<String,String> map=new HashMap<String, String>();
			List<String> lines = FileUtils.readLines(f);
			boolean key=true;
			boolean header=true;
			String keyStr=null;
			String valStr=null;
			for (String line:lines) {
				if (header) {
					if (line.startsWith("K")) {
						key=true;
						header=false;
					} else if (line.startsWith("V")) {
						key=false;
						header=false;
					}
				} else {
					if (key) {
						keyStr=line.trim();
					} else {
						valStr=line.trim();
						map.put(keyStr, valStr);
						keyStr=null;
						valStr=null;
					}
					header=true;
				}
			}
			
			if (map.containsKey("username")) {
				StoredAuth ret=new StoredAuth();
				ret.setEncrypted(map.containsKey("passtype")&&!map.get("passtype").equals("simple"));
				ret.setPassword(map.get("password"));
				ret.setUserName(map.get("username"));
				ret.setRealm(map.get("svn:realmstring"));
				return ret;
			} else {
				return null;
			}
		} catch (IOException e) {
			LOG.error("Failed to read auth file: "+f.getAbsolutePath(),e);
			return null;
		}
		
	}

	/**
	 * @return The directory to read SVN auth info from (useful for unit testing)
	 */
	public String getExternalDir() {
		return externalDir;
	}

	/**
	 * @param testDir The directory to read SVN auth info from (useful for unit testing)
	 */
	public void setExternalDir(String testDir) {
		this.externalDir = testDir;
	}

}
