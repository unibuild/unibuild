package net.unibld.core.task.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.unibld.core.task.ITaskRunner;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {
	String name();
	Class<? extends ITaskRunner<?>> runnerClass();
}
