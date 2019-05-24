//
// tweaked clone of BuildJavaHelper.java (14-Feb-2019)
//
package org.foundations.app.generate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.foundations.app.model.ColumnLoopTag;
import org.foundations.app.model.ColumnModel;
import org.foundations.app.model.IndexLoopTag;
import org.foundations.app.model.MetaTag;
import org.foundations.app.model.TableModel;
import org.foundations.app.model.UserSpecificationsModel;
import org.foundations.app.model.controller.TableModelController;
import org.foundations.app.util.DataTypeConversions;
import org.foundations.app.util.StringUtilities;

public class BuildJava extends BaseFoundation{
	
	//TODO create a BuildJavaObject that extents BaseFoundation
	//TODO then have this class extend BuildJavaObject instead of BaseFoundation
	//private static final String TEMPLATE = "DataObjectHelper";
	//private static final String TEMPLATE_FILENAME = "DataObjectHelper.template";

	private String author = null;
	private String schema = null;
	
	public static final String DEFAULT_AUTHOR = "unknown";
	
	private static final String TEMPLATE_FILE_EXT = ".template";
	
	private String objectTemplate = null;
	private String objectTemplateFilename = null;
	
	
	public BuildJava(Connection connectionIn, UserSpecificationsModel specsIn, String templateIn) {
		//TODO probably need to pass an EnvironmentSpecsModel...
		connection = connectionIn;
		userSpecs = specsIn;

		if (specsIn.getAuthor() != null && specsIn.getAuthor().length() > 0) {
			author = specsIn.getAuthor();
		} else {
			author = DEFAULT_AUTHOR;
		}
		
		objectTemplate = templateIn;
		objectTemplateFilename = templateIn + TEMPLATE_FILE_EXT;

	}
	
	/**
	 * Load the template, sub-template(s) and performance necessary tag replacments
	 * as needed.
	 * 
	 * @param tableModelIn
	 * @return
	 */
	public String buildJavaObjectForTable(TableModel tableModelIn) {
		StringBuilder builder = new StringBuilder();
		
		TableModelController modelController = new TableModelController(tableModelIn, userSpecs);
		
		// Load HelperObject.template into a String (probably a temp StringBuilder)
		//String template = getTemplate(null, TEMPLATE_FILENAME);
		String template = getTemplate(null, objectTemplateFilename);
		
		
		// Need to probably load all templates for datatypes found in table model columns...
		
		// perform search and replace based on tags
		template = staticSearchAndReplace(modelController, template);
		
		template = indexLoopSearchAndReplace(modelController, template, "uniqueIndexLoop");
		template = indexLoopSearchAndReplace(modelController, template, "non-uniqueIndexLoop");
		
		// perform search and replace on looping tags (for each column tags)
		template = columnLoopSearchAndReplace(modelController, template);

		// perform seach and replace on looping tags for columns that are NOT part of the primary key
		template = nonPKcolumnLoopSearchAndReplace(modelController, template);
		
		template = primaryKeyColumnLoopSearchAndReplace(modelController, template); //TODO added 15-feb-2019
		
		// Not using yet, but I'll still preserve the META tag info from the current template...
		MetaTag metaTag = new MetaTag(StringUtilities.getTag(template, "META", TAG_OPEN_DELIMIT, TAG_CLOSE_DELIMIT));
		
		// remove META tag from the top of the file
		template = StringUtilities.removeTag(template, "META", TAG_OPEN_DELIMIT, TAG_CLOSE_DELIMIT);
		
		//return builder.toString();
		return template;
	}
	
	/**
	 * DataObject templates have indexLoop tag to identify templates that should be used for both
	 * unique and non unique key generation logic.
	 * @param modelControllerIn
	 * @param templateIn
	 * @return
	 */
	private String indexLoopSearchAndReplace(TableModelController modelControllerIn, String templateIn, String tagNameIn) {
		String processName = "indexLoopSearchAndReplace";
		String updatedTemplate = templateIn;
		
		while (updatedTemplate.indexOf(TAG_OPEN_DELIMIT+tagNameIn) >= 0 ) {
			String currentIndexLoop = StringUtilities.getTag(updatedTemplate, tagNameIn, TAG_OPEN_DELIMIT, TAG_CLOSE_DELIMIT);
			IndexLoopTag indexLoop = new IndexLoopTag(tagNameIn, currentIndexLoop);
			
			// Need to load the template based on the specified templateName in the indexLoop
			String indexLoopTemplate = buildIndexTemplate(modelControllerIn, indexLoop.getTemplateName());
			updatedTemplate = updatedTemplate.replace(currentIndexLoop, indexLoopTemplate);
		}
		return updatedTemplate;
	}
	
	/**
	 * For now, I do not want to generate the unique keys automatically and
	 * I will simply return an empty string.
	 * 
	 * @param modelControllerIn
	 * @param templateNameIn
	 * @return
	 */
	private String buildIndexTemplate(TableModelController modelControllerIn, String templateNameIn) {
		String processName = "buildIndexTemplate";
		String workingIndexTemplate = "";
		
		// load template into workingIndexTemplate
		System.out.println("BuildJava."+processName+"  objectTemplate:["+ objectTemplate+"] templateNameIn:["+templateNameIn+"]");
		workingIndexTemplate = getTemplate(objectTemplate, templateNameIn); //who knows If this is correct
		
		// perform static replacements (not sure that there are any)
		//TODO left off here!!!
		
		// perform column looping replacements
		
		workingIndexTemplate = ""; //TODO remove this line if/when if I actually want this to do something besides remove the tag...
		
		return workingIndexTemplate;
	}
	
	/**
	 * Performs looping for all non-primary key columns (added 20-Feb-2019)
	 * @param modelControllerIn
	 * @param templateIn
	 * @return
	 */
	private String nonPKcolumnLoopSearchAndReplace(TableModelController modelControllerIn, String templateIn) {
		String processName = "nonPKcolumnLoopSearchAndReplace";
		String updatedTemplate = templateIn;
		
		while (updatedTemplate.indexOf("<<nonPrimaryKeyColumnLoop") >=0 ) {
			String currentColumnLoop = StringUtilities.getTag(updatedTemplate, "nonPrimaryKeyColumnLoop", TAG_OPEN_DELIMIT, TAG_CLOSE_DELIMIT);
			ColumnLoopTag columnLoop = new ColumnLoopTag(currentColumnLoop);
			
			// call buildNonPKColumnSearchParameters, passing the templateDir and separator
			String combinedLoop = buildNonPKColumnSearchParameters(modelControllerIn, columnLoop.getTemplateDirectory(), columnLoop.getSeparator()); //why not just pass columnLoop
		
			// replace columnLoop tag with results from buildSearchParameters
			updatedTemplate = updatedTemplate.replace(currentColumnLoop, combinedLoop); //NOTE: might not be able to alter string as it is controlled by the where loop...
		}
		
		return updatedTemplate;
		
	}
	
	
	/**
	 * 
	 * 
	 * Complex:
	 * <<columnLoop templateDirectory="searchParameters" separator=",">>
	 * <<columnLoop templateDirectory="callSearchParameters" separator=",">>
	 * <<columnLoop templateDirectory="callSearchESAPIParameters" separator=",">>
	 * 
	 * @param modelControllerIn
	 * @param templateIn
	 * @return
	 */
	private String columnLoopSearchAndReplace(TableModelController modelControllerIn, String templateIn) {
		String processName = "columnLoopSearchAndReplace";
		String updatedTemplate = templateIn;
		
		//14-feb-2019 just commented out below to test loop past 2 entries...
		//updatedTemplate = updatedTemplate.replace("<<columnLoop templateDirectory=\"searchParameters\" separator=\",\">>", buildSearchParametersCHEAT(modelControllerIn));
		
		while (updatedTemplate.indexOf("<<columnLoop") >=0 ) {
		
		// while next columnLoop tag
			String currentColumnLoop = StringUtilities.getTag(updatedTemplate, "columnLoop", TAG_OPEN_DELIMIT, TAG_CLOSE_DELIMIT);
			ColumnLoopTag columnLoop = new ColumnLoopTag(currentColumnLoop);
		// get templateDirectory value
			//String templateSubDirectory = StringUtilities.getAttributeValue(currentColumnLoop, "templateDirectory");
		// get separator value
			//String columnSeparator = StringUtilities.getAttributeValue(currentColumnLoop, "separator");
			
		// call buildSearchParameters, passing the templateDir and separator
			String combinedLoop = buildSearchParameters(modelControllerIn, columnLoop.getTemplateDirectory(), columnLoop.getSeparator()); //why not just pass columnLoop
		// replace columnLoop tag with results from buildSearchParameters
			updatedTemplate = updatedTemplate.replace(currentColumnLoop, combinedLoop); //NOTE: might not be able to alter string as it is controlled by the where loop...
		// end loop
		}
		
		return updatedTemplate;
		
	}
	
	// 15-feb-2019 created 
	// needed by xxxxAdmin.java and xxxxData.java
	private String primaryKeyColumnLoopSearchAndReplace(TableModelController modelControllerIn, String templateIn) {
		String processName = "primaryKeyColumnLoopSearchAndReplace";
		String updatedTemplate = templateIn;
		
		//14-feb-2019 just commented out below to test loop past 2 entries...
		//updatedTemplate = updatedTemplate.replace("<<columnLoop templateDirectory=\"searchParameters\" separator=\",\">>", buildSearchParametersCHEAT(modelControllerIn));
		
		while (updatedTemplate.indexOf("<<primaryKeyColumnLoop") >=0 ) {
		
		// while next columnLoop tag
			String currentColumnLoop = StringUtilities.getTag(updatedTemplate, "primaryKeyColumnLoop", TAG_OPEN_DELIMIT, TAG_CLOSE_DELIMIT);
			ColumnLoopTag columnLoop = new ColumnLoopTag(currentColumnLoop);
		// get templateDirectory value
			//String templateSubDirectory = StringUtilities.getAttributeValue(currentColumnLoop, "templateDirectory");
		// get separator value
			//String columnSeparator = StringUtilities.getAttributeValue(currentColumnLoop, "separator");
			
		// call buildSearchParameters, passing the templateDir and separator
			String combinedLoop = buildPrimaryKeyParameters(modelControllerIn, columnLoop.getTemplateDirectory(), columnLoop.getSeparator()); //why not just pass columnLoop
		// replace columnLoop tag with results from buildSearchParameters
			updatedTemplate = updatedTemplate.replace(currentColumnLoop, combinedLoop); //NOTE: might not be able to alter string as it is controlled by the where loop...
		// end loop
		}
		
		return updatedTemplate;
		
	}
	
	private String buildNonPKColumnSearchParameters(TableModelController modelControllerIn, String templateSubDirIn, String colSeparatorIn) {
		StringBuilder builder = new StringBuilder();

//		String searchParametersDirectory = objectTemplate+File.separator+templateSubDirIn+File.separator;
//		for (String dataType : modelControllerIn.getDistinctColumnTypes()) {
//			String dataTypeTemplate = getTemplate(searchParametersDirectory, DataTypeConversions.getTemplateNameForDataType(dataType.toUpperCase()));
//			modelControllerIn.addToSnippetMapping(dataType, dataTypeTemplate);
//		}
		
		String searchParametersDirectory = objectTemplate+File.separator+templateSubDirIn+File.separator;
		for (Integer columnType : modelControllerIn.getDistinctColumnTypesAsIntegers()) {
			String dataTypeTemplate = getTemplate(searchParametersDirectory, DataTypeConversions.getTemplateNameForColumnType(columnType.intValue()));
			modelControllerIn.addToSnippetMapping(columnType, dataTypeTemplate);
		}
		
		
		// loop thru each column and apply appropriate template (using static search and replace)
		for (ColumnModel column : modelControllerIn.getTableModel().getColumnModel()) {
			String columnName = column.getColumnName();
			if (modelControllerIn.getTableModel().getPrimaryKeyColumnModel().contains(column)) {
				continue; // if current column is part of the primary key list, move on to check the next column...
			}
			int columnType = column.getColumnType();
			String tempColumnSnippet = modelControllerIn.getCodeSnippet(columnType);
			tempColumnSnippet = tempColumnSnippet.replace("<<columnName>>", StringUtilities.convertToCamelCase(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<ColumnName>>", StringUtilities.convertTableNameToClassName(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<actualColumnName>>", columnName.toUpperCase());
			tempColumnSnippet = tempColumnSnippet.replace("<<COLUMN_NAME>>", columnName);
			tempColumnSnippet = tempColumnSnippet.replace("<<dataLength>>", ""+column.getDataLength());
			tempColumnSnippet = tempColumnSnippet.replace("<<decimalDigits>>", ""+column.getDataScale());
			tempColumnSnippet = tempColumnSnippet.replace("<<wholeNumberDigits>>", ""+(column.getDataPrecision()-column.getDataScale()));
			//tempColumnSnippet = tempColumnSnippet.replace("<<tableName>>", ""+modelControllerIn.getTableModel().getTableName()); //moved
			tempColumnSnippet = tempColumnSnippet.replace("<<columnNameWSpaces>>", StringUtilities.convertColumnNameToLowercaseWithSpaces(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<ColumnNameWSpaces>>", StringUtilities.convertColumnNameToUppercaseWithSpaces(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<objectName>>", modelControllerIn.getObjectNameAsCamelCase()); //Used by JSPs it looks like
			builder.append(tempColumnSnippet+colSeparatorIn);
		}
		if (builder.length() > 0 ) {
			builder.setLength(builder.length() - colSeparatorIn.length()); //remove last colSeparatorIn
		}
		return builder.toString();
	}
	

	/**
	 * 
	 * @param modelControllerIn
	 * @param templateSubDirIn 
	 * @param colSeparatorIn
	 * @return
	 */
	private String buildSearchParameters(TableModelController modelControllerIn, String templateSubDirIn, String colSeparatorIn) {
		StringBuilder builder = new StringBuilder();

//		String searchParametersDirectory = objectTemplate+File.separator+templateSubDirIn+File.separator;
//		for (String dataType : modelControllerIn.getDistinctColumnTypes()) {
//			String dataTypeTemplate = getTemplate(searchParametersDirectory, DataTypeConversions.getTemplateNameForDataType(dataType.toUpperCase()));
//			modelControllerIn.addToSnippetMapping(dataType, dataTypeTemplate);
//		}
		
		String searchParametersDirectory = objectTemplate+File.separator+templateSubDirIn+File.separator;
		for (Integer columnType : modelControllerIn.getDistinctColumnTypesAsIntegers()) {
			String dataTypeTemplate = getTemplate(searchParametersDirectory, DataTypeConversions.getTemplateNameForColumnType(columnType.intValue()));
			modelControllerIn.addToSnippetMapping(columnType, dataTypeTemplate);
		}
		
		
		// loop thru each column and apply appropriate template (using static search and replace)
		for (ColumnModel column : modelControllerIn.getTableModel().getColumnModel()) {
			String columnName = column.getColumnName();
			//String columnDataType = column.getDataType();
			int columnType = column.getColumnType();
			String tempColumnSnippet = modelControllerIn.getCodeSnippet(columnType);
			tempColumnSnippet = tempColumnSnippet.replace("<<columnName>>", StringUtilities.convertToCamelCase(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<ColumnName>>", StringUtilities.convertTableNameToClassName(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<actualColumnName>>", columnName.toUpperCase());
			tempColumnSnippet = tempColumnSnippet.replace("<<COLUMN_NAME>>", columnName);
			tempColumnSnippet = tempColumnSnippet.replace("<<dataLength>>", ""+column.getDataLength());
			tempColumnSnippet = tempColumnSnippet.replace("<<decimalDigits>>", ""+column.getDataScale());
			tempColumnSnippet = tempColumnSnippet.replace("<<wholeNumberDigits>>", ""+(column.getDataPrecision()-column.getDataScale()));
			//tempColumnSnippet = tempColumnSnippet.replace("<<tableName>>", ""+modelControllerIn.getTableModel().getTableName()); //moved
			tempColumnSnippet = tempColumnSnippet.replace("<<columnNameWSpaces>>", StringUtilities.convertColumnNameToLowercaseWithSpaces(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<ColumnNameWSpaces>>", StringUtilities.convertColumnNameToUppercaseWithSpaces(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<objectName>>", modelControllerIn.getObjectNameAsCamelCase()); //Used by JSPs it looks like
			//Spring templates
			tempColumnSnippet = tempColumnSnippet.replace("<<ObjectName>>", modelControllerIn.getObjectName());
			builder.append(tempColumnSnippet+colSeparatorIn);
		}
		if (builder.length() > 0 ) {
			builder.setLength(builder.length() - colSeparatorIn.length()); //remove last colSeparatorIn
		}
		return builder.toString();
	}
	
	private String buildPrimaryKeyParameters(TableModelController modelControllerIn, String templateSubDirIn, String colSeparatorIn) {
		StringBuilder builder = new StringBuilder();
		// build hashmap of templates for each data type used.
		modelControllerIn.getDistinctColumnTypesAsIntegers();
		//String searchParametersDirectory = TEMPLATE+File.separator+templateSubDirIn+File.separator;
		String searchParametersDirectory = objectTemplate+File.separator+templateSubDirIn+File.separator;
		for (Integer columnType : modelControllerIn.getDistinctColumnTypesAsIntegers()) {
			String dataTypeTemplate = getTemplate(searchParametersDirectory, DataTypeConversions.getTemplateNameForColumnType(columnType.intValue()));
			modelControllerIn.addToSnippetMapping(columnType, dataTypeTemplate); //TODO should this be adding to a new snippet mapping instead?
		}
		
		
		// loop thru each primary key column and apply appropriate template (using static search and replace){
		for (ColumnModel column : modelControllerIn.getTableModel().getPrimaryKeyColumnModel()) {
			String columnName = column.getColumnName();
			//String columnDataType = column.getDataType();
			int columnType = column.getColumnType();
			String tempColumnSnippet = modelControllerIn.getCodeSnippet(columnType);
			tempColumnSnippet = tempColumnSnippet.replace("<<columnName>>", StringUtilities.convertToCamelCase(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<ColumnName>>", StringUtilities.convertTableNameToClassName(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<actualColumnName>>", columnName.toUpperCase()); //TODO remove toUpperCase
			tempColumnSnippet = tempColumnSnippet.replace("<<COLUMN_NAME>>", columnName);
			tempColumnSnippet = tempColumnSnippet.replace("<<dataLength>>", ""+column.getDataLength());
			tempColumnSnippet = tempColumnSnippet.replace("<<decimalDigits>>", ""+column.getDataScale());
			tempColumnSnippet = tempColumnSnippet.replace("<<wholeNumberDigits>>", ""+(column.getDataPrecision()-column.getDataScale()));
			//tempColumnSnippet = tempColumnSnippet.replace("<<tableName>>", ""+modelControllerIn.getTableModel().getTableName()); //moved
			//<<columnNameWSpaces>> // not being replaced in the jsps!!!
			tempColumnSnippet = tempColumnSnippet.replace("<<columnNameWSpaces>>", StringUtilities.convertColumnNameToLowercaseWithSpaces(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<ColumnNameWSpaces>>", StringUtilities.convertColumnNameToUppercaseWithSpaces(columnName));
			tempColumnSnippet = tempColumnSnippet.replace("<<objectName>>", modelControllerIn.getObjectNameAsCamelCase()); //Used by JSPs it looks like
			// New Tags for Spring Foundations
			tempColumnSnippet = tempColumnSnippet.replace("<<ObjectName>>", modelControllerIn.getObjectName());
			builder.append(tempColumnSnippet+colSeparatorIn);
		}
		if (builder.length() > 0 ) {
			builder.setLength(builder.length() - colSeparatorIn.length()); //remove last colSeparatorIn
		}
		return builder.toString();
	}
	
	
	
	/**
	 * Common tags:
	 * 
	 * <<packageName>>
	 * <<ObjectName>>
	 * <<todaysDate>>
	 * <<author>>
	 * <<objectname>>
	 * 
	 * Complex:
	 * <<columnLoop templateDirectory="searchParameters" separator=",">>
	 * <<columnLoop templateDirectory="callSearchParameters" separator=",">>
	 * 
	 * <<META
extention="java"
description="Creates a subcomponent (Helper/Persister/DataObject)"
subdirectory="data/helper"
objectNameSuffix="Helper"
requiredTemplates="DataObject DataObjectPersister"
showInList="yes"
>>
	 * 
	 * @param modelControllerIn
	 * @param template
	 * @return
	 */
	private String staticSearchAndReplace(TableModelController modelControllerIn, String templateIn) {
		String updatedTemplate = templateIn;
		updatedTemplate = updatedTemplate.replace("<<packageName>>", userSpecs.getPackageName());
		updatedTemplate = updatedTemplate.replace("<<author>>", String.format("%-14s", userSpecs.getAuthor()));
		updatedTemplate = updatedTemplate.replace("<<ObjectName>>", modelControllerIn.getObjectName());
		updatedTemplate = updatedTemplate.replace("<<objectname>>", modelControllerIn.getObjectNameAsCamelCase());
		updatedTemplate = updatedTemplate.replace("<<objectName>>", modelControllerIn.getObjectNameAsCamelCase()); //Used by JSPs it looks like
		updatedTemplate = updatedTemplate.replace("<<todaysDate>>", StringUtilities.getTodaysDateMMddYYYY());
		updatedTemplate = updatedTemplate.replace("<<tableName>>", ""+modelControllerIn.getTableModel().getTableName());
		// New Tags for FOUNDATIONS
		updatedTemplate = updatedTemplate.replace("<<currentYear>>", StringUtilities.getCurrentYear());
		updatedTemplate = updatedTemplate.replace("<<copyright>>", userSpecs.getCopyright());
		updatedTemplate = updatedTemplate.replace("<<projectID>>", String.format("%-9s", userSpecs.getModLogJobId()));
		// New Tags for Spring Foundations
		updatedTemplate = updatedTemplate.replace("<<tablename>>", ""+modelControllerIn.getTableModel().getTableName().toLowerCase());
		
		
		return updatedTemplate; 
	}
	
	//TODO move to BuildJavaObject or BaseFoundation
	private String getTemplate(String subfolderIn, String templateNameIn) {
		String processName = "getTemplate";
		StringBuilder template = new StringBuilder();
		String templateDir = this.getSpringTemplatesDirectory();

		if (subfolderIn != null && subfolderIn.length() > 0) {
			//templateDir = templateDir+File.separator+subfolderIn;
			templateDir = templateDir+File.separator+subfolderIn+File.separator; //02-15-2019 swapout for indexLoop
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(templateDir+templateNameIn))) {
			String line = br.readLine();
			while (line != null) {
				template.append(line);
				template.append("\n"); //TODO consider \n\r
				line = br.readLine();
			}
		} catch (FileNotFoundException fnfe) {
			logEvent(processName, getClass().getSimpleName(), "ERROR", "Could not find template file:"+templateNameIn+" under:" + templateDir);
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			logEvent(processName, getClass().getSimpleName(), "ERROR", "Problem creating template file input streams: "+ioe.getMessage());
			ioe.printStackTrace();
		}
		
		return template.toString();
	}


}
