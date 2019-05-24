package org.foundations.app.model;

import java.util.List;

public class TableModel {
	
	String tableName;
	String schema;
	List<ColumnModel> columnModel;
	List<String> primaryKeyColumnsList;
	List<ColumnModel> primaryKeyColumnModel;

	//list<UK>
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public List<ColumnModel> getColumnModel() {
		return columnModel;
	}
	public void setColumnModel(List<ColumnModel> columnModel) {
		this.columnModel = columnModel;
	}
	public List<String> getPrimaryKeyColumnsList() {
		return primaryKeyColumnsList;
	}
	public void setPrimaryKeyColumnsList(List<String> primaryKeyColumnsList) {
		this.primaryKeyColumnsList = primaryKeyColumnsList;
	}
	public List<ColumnModel> getPrimaryKeyColumnModel() {
		return primaryKeyColumnModel;
	}
	public void setPrimaryKeyColumnModel(List<ColumnModel> primaryKeyColumnModel) {
		this.primaryKeyColumnModel = primaryKeyColumnModel;
	}
	
	
	@Override
	public String toString() {
		return "TableModel [tableName=" + tableName + ", schema=" + schema + ", columnModel=" + columnModel
				+ ", primaryKeyColumnsList=" + primaryKeyColumnsList + ", primaryKeyColumnModel="
				+ primaryKeyColumnModel + "]";
	}
	
	

}
