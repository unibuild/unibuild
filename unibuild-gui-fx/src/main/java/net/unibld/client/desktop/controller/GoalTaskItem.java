package net.unibld.client.desktop.controller;

class GoalTaskItem {
	private int idx;
	private String name;
	public GoalTaskItem(int idx, String name) {
		this.name=name;
		this.idx=idx;
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}