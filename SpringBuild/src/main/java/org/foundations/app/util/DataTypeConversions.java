package org.foundations.app.util;

public class DataTypeConversions {
	
	public static final String VARCHAR_PREFIX = "VARCHAR";
	public static final String VARCHAR_TEMPLATE = "VARCHAR.template";
	
	public static final String NUMBER_PREFIX = "NUMBER";
	public static final String DOUBLE_TEMPLATE = "DOUBLE.template"; // Good enough for now
	
	public static final String DATE_PREFIX = "DATE";
	public static final String DATE_TEMPLATE = "DATE.template";
	
	public static final String TIMESTAMP_PREFIX = "TIMESTAMP";
	public static final String TIMESTAMP_TEMPLATE = "TIMESTAMP.template";
	
	//postgreSQL dataTypes
	// (4) serial, 
	// (4) int4, 
	// (2) numeric
	// (14) varchar
	// (91) date
	// (93) timestamp
	// no wonder why it is best to use the columnType instead of dataType
	
	public static final String SERIAL_PREFIX = "SERIAL";
	public static final String SERIAL_TEMPLATE = "INTEGER.template";
	
	public static final String INT4_PREFIX = "INT4";
	public static final String INT4_TEMPLATE = "INTEGER.template";
	
	public static final String INTEGER_TEMPLATE = "INTEGER.template";
	//public static final String NUMERIC_TEMPLATE_2 = "NUMERIC.template";
	public static final String NUMERIC_TEMPLATE_2 = "DECIMAL.template";
	public static final String INTEGER_TEMPLATE_4 = "INTEGER.template";
	public static final String VARCHAR_TEMPLATE_14 = "VARCHAR.template";
	public static final String DATE_TEMPLATE_91 = "DATE.template";
	public static final String TIMESTAMP_TEMPLATE_93 = "TIMESTAMP.template";
	
	public static String getTemplateNameForColumnType(int columnTypeIn) {
		//TODO finish this up!!
		String templateFileName = "";
		if (columnTypeIn == 2) {
			templateFileName = NUMERIC_TEMPLATE_2;
		} else if (columnTypeIn == 4) {
			templateFileName = INTEGER_TEMPLATE_4;
		} else if (columnTypeIn < 10) {
			templateFileName = INTEGER_TEMPLATE;
		} else if (columnTypeIn == 14) {
			templateFileName = VARCHAR_TEMPLATE_14;	
		} else if (columnTypeIn < 20) {
			templateFileName = VARCHAR_TEMPLATE;
		} else if (columnTypeIn == 91) {
			templateFileName = DATE_TEMPLATE_91;
		} else if (columnTypeIn == 93) {
			templateFileName = TIMESTAMP_TEMPLATE_93;			
		} else if (columnTypeIn < 94) {
			templateFileName = TIMESTAMP_TEMPLATE;
		} else {
			System.err.println("DataTypeConversions.getTemplateNameForColumnType() -- Column Type:"+ columnTypeIn +" either not defined or tested yet!!!");
			throw new RuntimeException("DataTypeConversions.getTemplateNameForColumnType() -- UNDEFINED TEMPLATE FOR ColumnTYPE:["+columnTypeIn+"]");
		}
		
		return templateFileName;
	}
	
	
	public static String getTemplateNameForDataType(String dataTypeIn) {
		//TODO finish this up!!
		String templateFileName = "";
		if (dataTypeIn.startsWith(VARCHAR_PREFIX)) {
			templateFileName = VARCHAR_TEMPLATE;
		} else if (dataTypeIn.startsWith(NUMBER_PREFIX)) {
			templateFileName = DOUBLE_TEMPLATE;
		} else if (dataTypeIn.startsWith(DATE_PREFIX)) {
			templateFileName = DATE_TEMPLATE;
		} else if (dataTypeIn.startsWith(TIMESTAMP_PREFIX)) {
			templateFileName = TIMESTAMP_TEMPLATE;	
		} else if (dataTypeIn.startsWith(SERIAL_PREFIX)) {
			templateFileName = SERIAL_TEMPLATE;
		} else if (dataTypeIn.startsWith(INT4_PREFIX)) {
			templateFileName = INT4_TEMPLATE;	
		} else {
			System.err.println("DataTypeConversions.getTemplateNameForDataType() -- Data Type:"+ dataTypeIn +" either not defined or tested yet!!!");
			throw new RuntimeException("DataTypeConversions.getTemplateNameForDataType() -- UNDEFINED TEMPLATE FOR DATATYPE:["+dataTypeIn+"]");
		}
			
		return templateFileName;
	}

}
