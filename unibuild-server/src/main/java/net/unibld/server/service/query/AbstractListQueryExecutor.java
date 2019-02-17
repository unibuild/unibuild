package net.unibld.server.service.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class AbstractListQueryExecutor<T> implements IListQueryExecutor<T> {
	public abstract ServiceResult<T> getList(ListQueryInput input);
	public abstract EntityManager getEntityManager();

	protected String getDefaultOrderColumnName() {
		return "name";
	}
	
	protected boolean isDefaultOrderAscending() {
		return true;
	}

	protected boolean isBidirectionalLikeSupported() {
		return true;
	}
	protected List<T> getResultList(ListQueryInput input,Class<T> klazz) {
		EntityManager em = getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(klazz);
			Root<T> root = cq.from(klazz);
			cq.select(root);
			if (input.getFilters()!=null&&input.getFilters().size()>0) {
				Expression<Boolean> expr = createWhereExpression(cb,root,input.getFilters());
				if (expr!=null) {
					cq=cq.where(expr);
				}
			}
			
			if (input.getOrderColumn()!=null) {
				cq=cq.orderBy(createOrderExpression(cb,root,input.getOrderColumn(),input.isOrderAsc()));
			} else {
				if (isDefaultOrderAscending()) {
					cq=cq.orderBy(cb.asc(root.get(getDefaultOrderColumnName())));
				} else {
					cq=cq.orderBy(cb.desc(root.get(getDefaultOrderColumnName())));
				}
			}
			TypedQuery<T> q = em.createQuery(cq);
			q.setFirstResult(input.getFirst());
			q.setMaxResults(input.getMax());
			return q.getResultList();
			
		} finally { 
			em.close();
		}
	}
	protected List<Order> createOrderExpression(CriteriaBuilder cb,
			Root<T> root, String orderColumn, boolean orderAsc) {
		if (orderColumn==null) {
			throw new IllegalArgumentException("Order column was null");
		}
		
		
		Order order = orderAsc?cb.asc(root.get(orderColumn)):cb.desc(root.get(orderColumn));
		return Arrays.asList(order);

		
	}

	protected Expression<Boolean> createWhereExpression(CriteriaBuilder cb, Root<T> root,
			Map<String, Object> filters) {
		List<Predicate> preds=new ArrayList<Predicate>();
		Set<String> keySet = filters.keySet();
		for (String key:keySet) {
			Predicate p = createWherePredicate(cb,root,key,filters.get(key));
			if (p!=null) {
				preds.add(p);
			}
		}
		
		Predicate ret=null;
		for (Predicate p:preds) {
			if (ret==null) {
				ret=p;
			} else {
				ret=cb.and(ret,p);
			}
		}
		return ret;
	}

	protected Predicate createWherePredicate(CriteriaBuilder cb, Root<T> root,String key,Object val) {
		if (key==null) {
			throw new IllegalArgumentException("Key was null");
		}
		Path<String> isPath = root.get(key);
		if (val instanceof String) {
			if (isBidirectionalLikeSupported()) {
				return cb.like(cb.lower(isPath), "%"+((String)val).toLowerCase()+"%");
			} else {
				return cb.like(cb.lower(isPath), ((String)val).toLowerCase()+"%");
			}
		} else {
			return cb.equal(isPath, val);
		}
	}

	protected int getTotalCount(ListQueryInput input,Class<T> klazz) {
		EntityManager em = getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<T> root = cq.from(klazz);
			cq.select(cb.count(root));
			if (input.getFilters()!=null&&input.getFilters().size()>0) {
				Expression<Boolean> expr = createWhereExpression(cb,root,input.getFilters());
				if (expr!=null) {
					cq=cq.where(expr);
				}
			}
			
			Long count = em.createQuery(cq).getSingleResult();
			return count.intValue();
		} finally { 
			em.close();
		}
	} 
}
