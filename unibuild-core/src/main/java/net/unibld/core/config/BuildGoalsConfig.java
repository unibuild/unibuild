package net.unibld.core.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Configuration node for build goals.
 * @author andor
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "buildGoals")
public class BuildGoalsConfig {

	@XmlTransient
	private List<BuildGoalConfig> goals;

	

	/**
	 * @return User defined goals
	 */
	public List<BuildGoalConfig> getGoals() {
		return goals;
	}

	/**
	 * @param goals User defined goals
	 */
	public void setGoals(List<BuildGoalConfig> goals) {
		this.goals = goals;
	}

}
