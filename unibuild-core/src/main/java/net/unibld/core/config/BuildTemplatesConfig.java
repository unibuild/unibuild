package net.unibld.core.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Configuration node for build templates.
 * @author andor
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "buildTemplates")
public class BuildTemplatesConfig {

	@XmlElement(required=false,name="template")
	private List<BuildTemplateConfig> templates;

	/**
	 * @return Template configurations
	 */
	public List<BuildTemplateConfig> getTemplates() {
		return templates;
	}

	/**
	 * @param templates Template configurations
	 */
	public void setTemplates(List<BuildTemplateConfig> templates) {
		this.templates = templates;
	}
	
	


}
