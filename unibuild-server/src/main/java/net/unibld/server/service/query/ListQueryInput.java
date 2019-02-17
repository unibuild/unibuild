package net.unibld.server.service.query;

import java.util.Map;


public class ListQueryInput {
	public static final int DEFAULT_MAX = 20;
	
	private int first;
	private int max;
	private String orderColumn;
	private boolean orderAsc;
	private Map<String,Object> filters;

	
	
	public int getFirst() {
		return first;
	}
	public void setFirst(int first) {
		this.first = first;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public String getOrderColumn() {
		return orderColumn;
	}
	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}
	public boolean isOrderAsc() {
		return orderAsc;
	}
	public void setOrderAsc(boolean orderAsc) {
		this.orderAsc = orderAsc;
	}
	public boolean isEmpty() {
		return filters==null||filters.size()==0;
	}
	public static ListQueryInput emptyInput() {
		ListQueryInput i=new ListQueryInput();
		i.setMax(DEFAULT_MAX);
		return i;
	}
	public Map<String, Object> getFilters() {
		return filters;
	}
	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}
	
}
