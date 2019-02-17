package net.unibld.core.task.impl.java.maven;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ThirdPartyExecTask;
import net.unibld.core.util.PlatformHelper;

/**
 * A task that executes an Apache Maven task using {@link MavenTaskRunner} using mvn.bat (Windows) or mvn executable
 * on Unix-type systems.
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:28
 */
@Task(name="maven",runnerClass=MavenTaskRunner.class)
public class MavenTask extends ThirdPartyExecTask {
	
	private static final long serialVersionUID = 418828075431291037L;
	private String m2Home;
	private String javaHome;
	private String options;
	private String goals;


	private String update;
	private String skipTests;
	private String surefireSkipTests;
	

	/**
	 * Default constructor
	 */
	public MavenTask(){
		super();
	}


	/**
	 * @return An externally specified JAVA_HOME folder for Maven (instead of having to use an environment variable).
	 * If not specified, the default Java home is used.
	 */
	public String getJavaHome() {
		return javaHome;
	}

	/**
	 * @param javaHome An externally specified JAVA_HOME folder for Maven (instead of having to use an environment variable).
	 * If not specified, the default Java home is used.
	 */
	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	/**
	 * @return Other Maven options (before goals), separated by spaces
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * @param options Other Maven options (before goals), separated by spaces
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	
	@Override
	public String getPath() {
		if (m2Home!=null&&m2Home.trim().length()>0) {
			if (PlatformHelper.isWindows()) {
				return String.format("%s/bin/mvn.bat", m2Home);
			} else {
				return String.format("%s/bin/mvn", m2Home);
			}
		} 
		if (super.getPath()!=null) {
			return super.getPath();
		}
		return PlatformHelper.isWindows()?"mvn.bat":"mvn";
	}

	@Override
	public String getArgs() {
		StringBuilder b=new StringBuilder();
		if (options!=null&&options.trim().length()>0) {
			b.append(options);
			b.append(" ");
		}
		if (goals!=null&&goals.trim().length()>0) {
			b.append(goals);
		}
		return b.toString();
	}

	/**
	 * @return An externally specified M2_HOME folder (instead of having to use an environment variable).
	 * If not specified, mvn is attempted to be executed from the path.
	 */
	public String getM2Home() {
		return m2Home;
	}

	/**
	 * @param m2Home An externally specified M2_HOME folder (instead of having to use an environment variable).
	 * If not specified, mvn is attempted to be executed from the path.
	 */
	public void setM2Home(String m2Home) {
		this.m2Home = m2Home;
	}
	
	

	/**
	 * @return Maven goals, such as 'clean' or 'install', separated by spaces ('clean install')
	 */
	public String getGoals() {
		return goals;
	}

	/**
	 * @param goals Maven goals, such as 'clean' or 'install', separated by spaces ('clean install')
	 */
	public void setGoals(String goals) {
		this.goals = goals;
	}

	

	/**
	 * @return True if an update should be executed (-U switch)
	 */
	public String getUpdate() {
		return update;
	}

	/**
	 * @param update True if an update should be executed (-U switch)
	 */
	public void setUpdate(String update) {
		this.update = update;
	}

	/**
	 * @return True if tests should be skipped (-Dmaven.test.skip switch)
	 */
	public String getSkipTests() {
		return skipTests;
	}

	/**
	 * @param skipTests True if tests should be skipped (-Dmaven.test.skip switch)
	 */
	public void setSkipTests(String skipTests) {
		this.skipTests = skipTests;
	}
	
	/**
	 * @return True if skipping tests (-Dmaven.test.skip)
	 */
	public boolean isSkippingTests() {
		return "true".equals(skipTests);
	}
	/**
	 * @return True if skipping Surefire tests (-DskipTests)
	 */
	public boolean isSkippingSurefireTests() {
		return "true".equals(surefireSkipTests);
	}
	/**
	 * @return True if executing update (-U)
	 */
	public boolean isUpdating() {
		return "true".equals(update);
	}


	/**
	 * @return True if Surefire tests should be skipped (-DskipTests switch)
	 */
	public String getSurefireSkipTests() {
		return surefireSkipTests;
	}


	/**
	 * @param surefireSkipTests True if Surefire tests should be skipped (-DskipTests switch)
	 */
	public void setSurefireSkipTests(String surefireSkipTests) {
		this.surefireSkipTests = surefireSkipTests;
	}
}//end MavenTask