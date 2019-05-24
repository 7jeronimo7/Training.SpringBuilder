package org.foundations.app.model.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.foundations.app.model.ColumnModel;
import org.foundations.app.model.TableModel;
import org.foundations.app.model.UserSpecificationsModel;
import org.foundations.app.util.StringUtilities;

public class TableModelController {
	
	TableModel tableModel;
	UserSpecificationsModel userSpecs;
	
	HashMap<String,String> snippetMapping; //TODO Remove!!!
	HashMap<Integer,String> columnSnippetMapping;
	
	//TODO add keySnippetMapping??? to handle snippets within snippets...
	//TODO not sure if I need a map of a map, like FB did
	
	
	// contains business logic for a TableModel
	
	// get list of column types
	
	public TableModelController(TableModel tableModelIn, UserSpecificationsModel userSpecsIn) {
		tableModel = tableModelIn;
		userSpecs = userSpecsIn;
		snippetMapping = new HashMap<>();
		columnSnippetMapping = new HashMap<>();
	}
	
	public String getUnfilteredObjectName() {
		String name = StringUtilities.convertTableNameToClassName(tableModel.getTableName());
		
		return name;
	}
	
	public String getObjectName() {
		String name = null;
		if  (userSpecs != null && userSpecs.getPrefixToRemove() != null && 
			 userSpecs.getPrefixToRemove().trim().length() > 0 ) {
			name = StringUtilities.convertTableNameToClassName(tableModel.getTableName(), userSpecs.getPrefixToRemove());
		} else {
			name = StringUtilities.convertTableNameToClassName(tableModel.getTableName());
		}
		return name;
	}
	
	public String getUnfilteredObjectNameAsCamelCase() {
		String name = StringUtilities.convertToCamelCase(tableModel.getTableName());
		
		return name;
	}

	public String getObjectNameAsCamelCase() {
		String name = StringUtilities.convertToCamelCase(tableModel.getTableName(), userSpecs.getPrefixToRemove());
		
		return name;
	}
	
	
	public List<String> getDistinctColumnTypesA() {
		List<String> colTypes = new ArrayList<>();
		for (ColumnModel columns : tableModel.getColumnModel()) {
			if (!colTypes.contains(columns.getDataType()))
				colTypes.add(columns.getDataType());
		}
		
		return colTypes;
	}
	
	public List<Integer> getDistinctColumnTypesAsIntegers() {
		List<Integer> colTypes = new ArrayList<>();
		for (ColumnModel columns : tableModel.getColumnModel()) {
			if (!colTypes.contains(new Integer(columns.getColumnType()))) //upconvert to Integer Object
				colTypes.add(columns.getColumnType());
		}
		
		return colTypes;
	}
	
	public void addToSnippetMappingOLD(String dataTypeIn, String snippetIn) {
		// no need to check for duplicates as the old key value is replaced by the new value
		snippetMapping.put(dataTypeIn, snippetIn);
	}
	public void addToSnippetMapping(Integer columnTypeIn, String snippetIn) {
		// no need to check for duplicates as the old key value is replaced by the new value
		columnSnippetMapping.put(columnTypeIn, snippetIn);
	}
	
	public String getCodeSnippetOLD(String dataTypeIn) {
		return snippetMapping.get(dataTypeIn);
	}
	
	public String getCodeSnippet(Integer columnTypeIn) {
		return columnSnippetMapping.get(columnTypeIn);
	}

	public TableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}


	public HashMap<Integer, String> getColumnSnippetMapping() {
		return columnSnippetMapping;
	}


	public void setColumnSnippetMapping(HashMap<Integer, String> columnSnippetMapping) {
		this.columnSnippetMapping = columnSnippetMapping;
	}

	
	
	
}
