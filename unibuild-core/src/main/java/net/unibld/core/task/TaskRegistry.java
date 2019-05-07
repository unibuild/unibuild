package net.unibld.core.task;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.BuildTask;
import net.unibld.core.build.BuildException;
import net.unibld.core.task.annotations.Task;

/**
 * A registry for tasks that can be run in a UniBuild context.<br>
 * By default it contains all classes under the base package net.unibuild, annotated with the 
 * {@link Task} annotation.
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:35
 */
@Component
public class TaskRegistry {
	private static final Logger LOG=LoggerFactory.getLogger(TaskRegistry.class);
	
	
	/**
	 * Initializes the registry using recursive introspection under the net.unibld base package
	 */
	@PostConstruct
	public void initialize() {
		LOG.debug("Initializing task registry...");
		introspect("net.unibld");
		LOG.info("{} task types have been initialized",tasksByName.size());
	}
	public int introspect(String packagePath) {
		Reflections reflections = new Reflections(packagePath);
		Set<Class<?>> annotated = 
	               reflections.getTypesAnnotatedWith(Task.class);
		
		int cnt=0;
		for (Class<?> klazz:annotated) {
			try {
				LOG.debug("Loading task: {}...",klazz.getName());
				loadClass(klazz);
				LOG.info("Loaded task: {}",klazz.getName());
				cnt++;
			} catch (Exception ex) {
				LOG.error("Failed to load task: {} - error: {}",klazz.getName(),ex.getMessage());
			}
		}
		return cnt;
	}
	@SuppressWarnings("unchecked")
	private void loadClass(Class<?> klazz) {
		if (klazz==null) {
			throw new IllegalArgumentException("Class was null");
		}
		if (!BuildTask.class.isAssignableFrom(klazz)) {
			throw new IllegalArgumentException("Class is not an instance of BuildTask: "+klazz.getName());
		}
		
		Task annotation = klazz.getAnnotation(Task.class);
		if (annotation==null) {
			throw new IllegalArgumentException("Class does not have a Task annotation: "+klazz.getName());
		}
		Class<? extends BuildTask> taskClass=(Class<? extends BuildTask>) klazz;
		tasksByName.put(annotation.name(), taskClass);
		if (annotation.runnerClass()==null) {
			throw new IllegalArgumentException("Runner class not specified: "+klazz.getName());
		}
		taskNamesByClasses.put(taskClass,annotation.name());
		
	}
	private Map<String,Class<? extends BuildTask>> tasksByName;
	private Map<Class<? extends BuildTask>,String> taskNamesByClasses;
	
	public TaskRegistry(){
		tasksByName=new Hashtable<>();
		taskNamesByClasses=new Hashtable<>();
	}

	/**
	 * Creates a new {@link BuildTask} instance with specified name
	 * @param taskName Unique task name
	 * @return New task instance
	 * @throws BuildException If any error occurs during instantiation 
	 * 
	 */
	public BuildTask newTaskInstance(String taskName) {
		Class<? extends BuildTask> klazz = tasksByName.get(taskName);
		if (klazz==null) {
			throw new IllegalArgumentException("Task name not found: "+taskName);
		}
		try {
			return klazz.newInstance();
		} catch (Exception e) {
			LOG.error("Failed to instantiate build task: "+klazz.getName(),e);
			throw new BuildException("Failed to instantiate build task: "+klazz.getName(),e);
		} 
	}
	
	
	/**
	 * Returns the unique task name for a task class
	 * @param klazz {@link BuildTask} class
	 * @return The unique task name
	 */
	public String getTaskNameByClass(Class<? extends BuildTask> klazz) {
		return taskNamesByClasses.get(klazz);
	}
	/**
	 * Returns the task class for a unique task name
	 * @param name Unique task name
	 * @return Task class
	 */
	public Class<? extends BuildTask> getTaskClassByName(String name) {
		return tasksByName.get(name);
	}
}//end TaskRegistry