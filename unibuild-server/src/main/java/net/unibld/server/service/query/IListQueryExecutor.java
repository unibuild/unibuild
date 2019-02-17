package net.unibld.server.service.query;

public interface IListQueryExecutor<T> {
	ServiceResult<T> getList(ListQueryInput input);

}
