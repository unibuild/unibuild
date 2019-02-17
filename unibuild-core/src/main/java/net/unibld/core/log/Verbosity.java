package net.unibld.core.log;

public class Verbosity {
	public Verbosity(boolean verbose, boolean trace) {
		super();
		this.verbose = verbose;
		this.trace = trace;
	}
	private boolean verbose;
	private boolean trace;
	public boolean isVerbose() {
		return verbose;
	}
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public boolean isTrace() {
		return trace;
	}
	public void setTrace(boolean trace) {
		this.trace = trace;
	}
}
