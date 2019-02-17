package net.unibld.core.task.impl.java.gradle;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ThirdPartyExecTask;

/**
 * A task that executes a Gradle task using {@link GradleTaskRunner}.
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:28
 */
@Task(name="gradle",runnerClass=GradleTaskRunner.class)
public class GradleTask extends ThirdPartyExecTask {
	private String gradleHome;
	private String javaHome;
	private String options;
	private String task;


	public GradleTask(){

	}


	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	
	@Override
	public String getPath() {
		if (gradleHome!=null&&gradleHome.trim().length()>0) {
			return String.format("%s/bin/gradle", gradleHome);
		} 
		if (super.getPath()!=null) {
			return super.getPath();
		}
		return "gradle";
	}

	@Override
	public String getArgs() {
		StringBuilder b=new StringBuilder();
		if (options!=null&&options.trim().length()>0) {
			b.append(options);
			b.append(" ");
		}
		if (task!=null&&task.trim().length()>0) {
			b.append(task);
		}
		return b.toString();
	}


	public String getGradleHome() {
		return gradleHome;
	}


	public void setGradleHome(String gradleHome) {
		this.gradleHome = gradleHome;
	}


	public String getTask() {
		return task;
	}


	public void setTask(String task) {
		this.task = task;
	}

	
	



}//end MavenTask