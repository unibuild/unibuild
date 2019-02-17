package net.unibld.core.task;

import net.unibld.core.BuildTask;

public interface ITaskRunner<T extends BuildTask> {
	void run(T task);
}
