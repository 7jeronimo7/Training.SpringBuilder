package org.foundations.app.model;

public class UserSpecificationsModel {
	
	private String author;
	private String version;
	private String packageName;
	private String copyright;
	private String modLogJobId;
	private String prefixToRemove;
	
	// consider adding tablename, schema, etc.
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public String getModLogJobId() {
		return modLogJobId;
	}
	public void setModLogJobId(String modLogJobId) {
		this.modLogJobId = modLogJobId;
	}
	public String getPrefixToRemove() {
		return prefixToRemove;
	}
	public void setPrefixToRemove(String prefixToRemove) {
		this.prefixToRemove = prefixToRemove;
	}
	@Override
	public String toString() {
		return "UserSpecificationsModel [author=" + author + ", version=" + version + ", packageName=" + packageName
				+ ", copyright=" + copyright + ", modLogJobId=" + modLogJobId + ", prefixToRemove=" + prefixToRemove
				+ "]";
	}


}
