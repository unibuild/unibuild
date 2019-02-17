package net.unibld.core.task.impl.server;

import java.util.List;

import net.unibld.core.config.InstanceConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.task.impl.AbstractExecTaskRunner;

/**
 * A base class for running server shell scripts. Server instances can be
 * configured in project.xml and a server shell task needs an instance to 
 * be specified to run on.
 * @author andor
 *
 */
public abstract class ServerTaskRunner<T extends ServerTask> extends AbstractExecTaskRunner<T> {
	protected InstanceConfig getInstanceConfig(T task) {
		
		String instanceName=task.getInstanceName();
		ProjectConfig pc = getProjectConfig(task);
		List<InstanceConfig> instanceConfigs = pc.getInstances().getInstanceConfigs();
		if (instanceConfigs==null||instanceConfigs.size()==0) {
			throw new IllegalArgumentException("No InstanceConfig nodes defined");
		}
		if (instanceConfigs.size()==1) {
			instanceName=instanceConfigs.get(0).getNodeName();
		}
		if (instanceName==null) {
			instanceName=pc.getInstances().getDefaultNode();
		}
		if (instanceName==null) {
			throw new IllegalArgumentException("Instance argument name not specified and no default name found");
		}
		return getInstanceConfigByName(task,instanceName);
	}

}
