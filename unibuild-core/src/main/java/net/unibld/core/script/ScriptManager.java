package net.unibld.core.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.jce.MD5;

import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.util.PlatformHelper;

@Component
public class ScriptManager {
	private static final Logger LOGGER=LoggerFactory.getLogger(ScriptManager.class);
	
	private static final String DEFAULT_SCRIPTS_DIR_UNIX="/etc/unibld/scripts";
	private static final String DEFAULT_SCRIPTS_DIR_WINDOWS="c:/ProgramData/UniBuild/scripts";
	
	@Value("${scripts.dir:}")
	private String scriptsDir;
	
	@Autowired
	private CmdExecutor executor;
	
	public File getScriptsDir() {
		File dir=new File(getScriptsDirPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	
	public File getScriptsDirForPlugin(String pluginName) {
		File dir=new File(FilenameUtils.concat(getScriptsDirPath(),pluginName));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	private String getScriptsDirPath() {
		if (!StringUtils.isEmpty(scriptsDir)) {
			if (PlatformHelper.isWindows()) {
				return DEFAULT_SCRIPTS_DIR_WINDOWS;
			} else {
				return DEFAULT_SCRIPTS_DIR_UNIX;
			}
		}
		return scriptsDir;
	}
	
	public File installScriptIfNecessary(InputStream source, String fileName) {
		LOGGER.info("Installing script {} to: {}",fileName,getScriptsDirPath());
		return extractScriptIfNecessary(source, FilenameUtils.concat(getScriptsDir().getAbsolutePath(), fileName));
	}
	
	public File installScriptIfNecessary(InputStream source, String pluginName, String fileName) {
		LOGGER.info("Installing script {} for plugin {} to: {}",fileName,pluginName, getScriptsDirPath());
		return extractScriptIfNecessary(source, FilenameUtils.concat(getScriptsDirForPlugin(pluginName).getAbsolutePath(), fileName));
	}
	
	private File extractScriptIfNecessary(InputStream is,String targetPath) {
		LOGGER.info("Checking script: {}...",targetPath);
		if (is==null) {
			throw new IllegalArgumentException("Input stream was null");
		}
		
		try {
			byte[] bytes = IOUtils.toByteArray(is);
			boolean needsExtract=false;
			File target=new File(targetPath);
			if (!target.exists()||!target.isFile()) {
				LOGGER.info("Target file not existing: {}",targetPath);
				needsExtract=true;
			} else {
				String hash = DigestUtils.md5Hex(FileUtils.readFileToByteArray(target));
				String hash2 =  DigestUtils.md5Hex(bytes);
				if (hash.equals(hash2)) {
					LOGGER.info("Script is the same as target {}, skipping...",targetPath);
				} else {
					LOGGER.info("Script differs from target {}...",targetPath);
					needsExtract=true;
				}
			}
			
			if (needsExtract) {
				LOGGER.info("Extracting script to target: {}...",targetPath);
				FileUtils.writeByteArrayToFile(target, bytes);
				
				if (!PlatformHelper.isWindows()) {
					CmdContext ctx = new CmdContext();
					try {
						executor.execCmd(ctx, String.format("chmod 700 %s",target.getAbsolutePath()));
					} catch (Exception e) {
						LOGGER.error("Failed to chmod", e);
					}
				}
				
			}
			return target;
		} catch (IOException e) {
			LOGGER.error("Failed to check script",e);
			throw new IllegalStateException("Failed to check script",e);
		}
	}
	
}
