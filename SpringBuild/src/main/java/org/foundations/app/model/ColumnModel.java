package org.foundations.app.model;

public class ColumnModel {
	
	String columnName;
	String dataType;
	int columnType;
	int dataLength;
	int dataPrecision;
	int dataScale;
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public int getColumnType() {
		return columnType;
	}
	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}
	public int getDataLength() {
		return dataLength;
	}
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	public int getDataPrecision() {
		return dataPrecision;
	}
	public void setDataPrecision(int dataPrecision) {
		this.dataPrecision = dataPrecision;
	}
	public int getDataScale() {
		return dataScale;
	}
	public void setDataScale(int dataScale) {
		this.dataScale = dataScale;
	}
	
	@Override
	public String toString() {
		return "ColumnModel [columnName=" + columnName + ", dataType=" + dataType + ", columnType=" + columnType
				+ ", dataLength=" + dataLength + ", dataPrecision=" + dataPrecision + ", dataScale=" + dataScale + "]";
	}
	
	

}
