package net.unibld.core.util.version;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.util.BuildNumberUtils;
import net.unibld.core.util.maven.PomHelper;

public class VersionHelper {
	
	private static final Logger LOG=LoggerFactory.getLogger(VersionHelper.class);

	public static String getVersionFromPomOrProperties(VersionContext ctx) {
		StringBuilder versionBuilder=new StringBuilder();
		
		if (ctx.isPom()) {
			if (StringUtils.isEmpty(ctx.getPomFile())) {
				throw new IllegalStateException("POM file not specified");
			}
			try {
				versionBuilder.append(PomHelper.getVersionFromPomFile(ctx.getPomFile(),ctx.getTaskLogger()));
			} catch (Exception e) {
				if (ctx.getTaskLogger()!=null) {
					ctx.getTaskLogger().logError("Could not read version from POM: "+ctx.getPomFile(),e);
				}
				throw new IllegalStateException("Could not read version from POM: "+ctx.getPomFile(),e);
			} 
		}
		if (ctx.isBuildNum()) {
			if (ctx.isPom()) {
				versionBuilder.append('.');
			}
			versionBuilder.append(BuildNumberUtils.getBuildNumberFromProperties(ctx.getBuildNumberProperties(),ctx.getBuildNumberPropertyName()));
		}
		return versionBuilder.toString();
	}


	
}
