/**
 * Created on: 2014.06.03.
 * Created by: Andor Toth 
 */
package net.unibld.server.service.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class ServiceResult<T> implements Serializable {
	
	
	private List<T> resultList;
	private int totalCount;

	public ServiceResult() {
	}

	public ServiceResult(T object) {
		this.resultList=new ArrayList<T>();
		this.resultList.add(object);
		
	}


	public ServiceResult(List<T> objectList) {
		this.resultList=objectList;
	}


	public ServiceResult(List<T> objectList, int totalCount) {
		this.resultList=objectList;
		this.totalCount=totalCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getResultList() {
		return resultList;
	}

	public void setResultList(List<T> resultList) {
		this.resultList = resultList;
	}


}
