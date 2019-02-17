package net.unibld.server.web.rest.remote.extract;

import java.io.File;

import net.unibld.core.util.Zip;
import net.unibld.server.web.rest.remote.RemoteBuildException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UnzipExtractor implements IExtractor {
	private static final Logger LOG = LoggerFactory.getLogger(UnzipExtractor.class);

	public String extract(String filePath, String targetDir) {

		File f = new File(filePath);

		if (!f.exists()) {
			LOG.warn("Cannot find file: " + filePath);
			return null;
		}

		LOG.info(
				String.format("Extracting: %s to %s...", filePath, targetDir));
		try {
			Zip.unzip(filePath, targetDir);
			return targetDir;
		} catch (Exception ex) {
			String msg = String.format("Failed to unzip %s to %s", filePath,
					targetDir);
			LOG.error(msg, ex);
			
			throw new RemoteBuildException(msg, ex);
		}

	}
}
