package net.unibld.server.service.mail;

public class MultiMailSendResult {
	private int sent;
	private int failed;
	public int getSent() {
		return sent;
	}
	public void setSent(int sent) {
		this.sent = sent;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	public void incSent() {
		sent++;
	}
	public void incFailed() {
		failed++;
	}
}