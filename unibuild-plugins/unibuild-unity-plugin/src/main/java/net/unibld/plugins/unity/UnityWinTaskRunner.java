package net.unibld.plugins.unity;

import org.apache.commons.lang.StringUtils;

import net.unibld.core.build.BuildException;


/**
 * An abstract task runner for executing a Unity task on Windows
 * @author andor
 *
 */
public abstract class UnityWinTaskRunner<T extends UnityWinTask> extends AbstractUnityTaskRunner<T>{
	protected String getOptions(T task) {
		String base=super.getOptions(task);
		
		if (task.isForceD3d11()&&task.isForceOpenGl()) {
			throw new BuildException("DirectX 11 and OpenGL cannot be forced at the same time");
		}
		
		StringBuilder b=new StringBuilder();
		int i=0;
		
		
		if (task.isForceD3d11()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-force-d3d11");
			i++;
		}
		if (task.isForceOpenGl()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-force-opengl");
			i++;
		}
		
		if (StringUtils.isEmpty(base)) {
			return b.toString();
		}
		return b.toString()+" "+base;
		
	}
}
