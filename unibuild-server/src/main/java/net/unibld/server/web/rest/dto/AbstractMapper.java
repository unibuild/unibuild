package net.unibld.server.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

import net.unibld.core.persistence.model.Project;

public abstract class AbstractMapper<E,D> {
	public List<D> toDtoList(List<E> list) {
		if (list==null) {
			return null;
		}
		List<D> ret=new ArrayList<D>();
		for (E e: list) {
			ret.add(toDto(e));
		}
		return ret;
	}
	public D toDto(E e) {
		if (e==null) {
			return null;
		}
		D dto=createDto();
		fillDto(dto, e);
		return dto;
	}
	protected abstract void fillDto(D dto, E e);
	protected abstract D createDto();
}
