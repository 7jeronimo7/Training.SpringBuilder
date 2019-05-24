package org.foundations.app.model;

import org.foundations.app.util.StringUtilities;

public class ColumnLoopTag {
	
	
	private String templateDirectory;
	private String separator;
	
	public ColumnLoopTag() {
		
	}
	
	public ColumnLoopTag(String columnLoopTagIn) {
		templateDirectory = StringUtilities.getAttributeValue(columnLoopTagIn, "templateDirectory");
		separator = StringUtilities.getAttributeValue(columnLoopTagIn, "separator");
	}
	
	public String getTemplateDirectory() {
		return templateDirectory;
	}
	public void setTemplateDirectory(String templateDirectory) {
		this.templateDirectory = templateDirectory;
	}
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	
	

}
