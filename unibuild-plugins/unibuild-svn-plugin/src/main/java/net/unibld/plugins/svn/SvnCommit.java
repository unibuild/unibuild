package net.unibld.plugins.svn;

import net.unibld.core.build.IBuildContextAttributeContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SvnCommit extends SvnCommand {
	private static final Logger LOG=LoggerFactory.getLogger(SvnCommit.class);
	private String comment;

	@Override
	public String getArguments() {
		String args = String.format("%s -m \"%s\"",getCommand(),comment);
		LOG.info("Commit arguments: "+args);
		
		return args;
	}

	@Override
	public String getCommand() {
		return "commit";
	}

	@Override
	public void prepare(IBuildContextAttributeContainer attributeContainer,SvnTask task) {
		this.userName=task.getUserName();
		this.comment=task.getComment();
		if (this.comment==null) {
			this.comment="Automated build";
		}
		this.useBuiltinStore=task.isUseBuiltinStore();
		useCheckoutDirAsWorkingDir(attributeContainer,task);
	}
	
	


}
