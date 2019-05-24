package org.foundations.app.model;

import org.foundations.app.util.StringUtilities;

public class MetaTag {
	
	private String extention;
	private String description;
	private String subdirectory;
	private String objectNameSuffix;
	private String requiredTemplates; // probably wont really use this
	private String showInList; // probably wont use this
	
	public MetaTag() {
		
	}
	
	public MetaTag(String metaTagIn) {
		extention = StringUtilities.getAttributeValue(metaTagIn, "extention");
		description = StringUtilities.getAttributeValue(metaTagIn, "description");
		subdirectory = StringUtilities.getAttributeValue(metaTagIn, "subdirectory");
		objectNameSuffix = StringUtilities.getAttributeValue(metaTagIn, "objectNameSuffix");
		requiredTemplates = StringUtilities.getAttributeValue(metaTagIn, "requiredTemplates");
		showInList = StringUtilities.getAttributeValue(metaTagIn, "showInList");
		
	}
	
	public String getExtention() {
		return extention;
	}
	public void setExtention(String extention) {
		this.extention = extention;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSubdirectory() {
		return subdirectory;
	}
	public void setSubdirectory(String subdirectory) {
		this.subdirectory = subdirectory;
	}
	public String getObjectNameSuffix() {
		return objectNameSuffix;
	}
	public void setObjectNameSuffix(String objectNameSuffix) {
		this.objectNameSuffix = objectNameSuffix;
	}
	public String getRequiredTemplates() {
		return requiredTemplates;
	}
	public void setRequiredTemplates(String requiredTemplates) {
		this.requiredTemplates = requiredTemplates;
	}
	public String getShowInList() {
		return showInList;
	}
	public void setShowInList(String showInList) {
		this.showInList = showInList;
	}
	@Override
	public String toString() {
		return "MetaTag [extention=" + extention + ", description=" + description + ", subdirectory=" + subdirectory
				+ ", objectNameSuffix=" + objectNameSuffix + ", requiredTemplates=" + requiredTemplates
				+ ", showInList=" + showInList + "]";
	}
	
	

}
