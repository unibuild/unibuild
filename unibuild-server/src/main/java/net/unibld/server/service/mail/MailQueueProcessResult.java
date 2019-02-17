package net.unibld.server.service.mail;

public class MailQueueProcessResult {
	private int succeeded;
	private int failed;
	private int total;
	public int getSucceeded() {
		return succeeded;
	}
	public void setSucceeded(int succeeded) {
		this.succeeded = succeeded;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public void addSuccess() {
		succeeded++;
	}
	public void addError() {
		failed++;
	}
}
