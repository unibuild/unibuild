package net.unibld.core.log;

import net.unibld.core.config.LoggerConfig;

public abstract class AbstractLogger implements IBuildLogger {
	protected boolean verbose;
	protected boolean trace;
	protected String buildId;
	@Override
	public void configure(LoggerConfig lc,boolean verbose,boolean trace) {
		this.verbose=verbose;
		this.trace=trace;
		
	}
	public boolean isVerbose() {
		return verbose;
	}
	public boolean isTrace() {
		return trace;
	}
	public String getBuildId() {
		return buildId;
	}
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
}
