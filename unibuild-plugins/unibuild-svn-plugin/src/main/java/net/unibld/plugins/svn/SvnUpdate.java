package net.unibld.plugins.svn;

import net.unibld.core.build.IBuildContextAttributeContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SvnUpdate extends SvnCommand {
	private static final Logger LOG=LoggerFactory.getLogger(SvnCommit.class);
	@Override
	public String getArguments() {
		return getCommand();
	}

	@Override
	public String getCommand() {
		return "update";
	}

	@Override
	public void prepare(IBuildContextAttributeContainer attributeContainer,SvnTask task) {
		this.useBuiltinStore=task.isUseBuiltinStore();
		useCheckoutDirAsWorkingDir(attributeContainer,task);
	}
	

}
