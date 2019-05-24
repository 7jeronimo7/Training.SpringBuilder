package org.foundations.app.model;

import org.foundations.app.util.StringUtilities;

public class IndexLoopTag {
	
	private String tagName; // dont think I need it, but it will aid in troubleshooting/debugging
	private String templateName;
	
	public IndexLoopTag() {
		
	}
	
	public IndexLoopTag(String tagNameIn, String indexColumnTagIn) {
		tagName = tagNameIn;
		templateName = StringUtilities.getAttributeValue(indexColumnTagIn, "templateName");
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	@Override
	public String toString() {
		return "IndexLoopTag [templateName=" + templateName + "]";
	}
	
	

}
