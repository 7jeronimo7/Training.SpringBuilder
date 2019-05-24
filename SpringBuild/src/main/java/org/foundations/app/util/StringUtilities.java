package org.foundations.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StringUtilities {
	
	public static String convertColumnNameToLowercaseWithSpaces(String columnNameIn) {
		String tempValue = columnNameIn;
		if (columnNameIn != null && columnNameIn.length() > 0) {
			tempValue = tempValue.replace("_", " ");
			tempValue = tempValue.toLowerCase();
			
		}
		return tempValue;
	}
	
	/**
	 * Not sure this is correct... but I will use it anyhow for 
	 * <<ColumnNameWSpaces>> that is used with DATE fields in the Persister object.
	 * @param columnNameIn
	 * @return
	 */
	public static String convertColumnNameToUppercaseWithSpaces(String columnNameIn) {
		String tempValue = columnNameIn;
		if (columnNameIn != null && columnNameIn.length() > 0) {
			tempValue = tempValue.replace("_", " ");
			tempValue = tempValue.toUpperCase();
		}
		return tempValue;
	}
	
	/**
	 * Example: MY_TABLE_NAME converts to myTableName
	 * 
	 * @param tableNameIn
	 * @return
	 */
	public static String convertToCamelCase(String tableNameIn) {
		StringBuilder sb = new StringBuilder();
		String[] str = tableNameIn.split("_");
		boolean firstTime = true;
		for (String temp : str) {
			if (firstTime) {
				sb.append(temp.toLowerCase());
				firstTime = false;
			} else {
				sb.append(Character.toUpperCase(temp.charAt(0)));
				sb.append(temp.substring(1).toLowerCase());
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Should convert MY_TABLE_NAME, MY
	 * to simply: tableName
	 * 
	 * @param tableNameIn
	 * @param prefixToRemoveIn
	 * @return
	 */
	public static String convertToCamelCase(String tableNameIn, String prefixToRemoveIn) {
		StringBuilder sb = new StringBuilder();
		String[] str = tableNameIn.split("_");
		boolean firstTime = true;
		
		String prefix="";
		if (prefixToRemoveIn != null && prefixToRemoveIn.trim().length() > 0) {
			prefix = prefixToRemoveIn.replace("_", "");
		}
		
		for (String temp : str) {
			if (prefix.trim().length() > 0 && temp.equals(prefix)) {
				continue; // do not append
			}
			if (firstTime) {
				sb.append(temp.toLowerCase());
				firstTime = false;
			} else {
				if (temp != null && temp.length() > 1) {
					sb.append(Character.toUpperCase(temp.charAt(0)));
					sb.append(temp.substring(1).toLowerCase());					
				} else if (temp != null && temp.length() > 1) {
					sb.append(Character.toUpperCase(temp.charAt(0)));
				}
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Example: MY_TABLE_NAME converts to MyTableName
	 * 
	 * @param tableNameIn
	 * @return
	 */
	public static String convertTableNameToClassName(String tableNameIn) {
		StringBuilder sb = new StringBuilder();
		String[] str = tableNameIn.split("_");
		for (String temp : str) {
				sb.append(Character.toUpperCase(temp.charAt(0)));
				sb.append(temp.substring(1).toLowerCase());
		}
		
		return sb.toString();
	}
	
	/**
	 * Should convert: MY_FIRST_TABLE, MY
	 * to: FirstTable
	 * 
	 * @param tableNameIn
	 * @param prefixToRemoveIn
	 * @return
	 */
	public static String convertTableNameToClassName(String tableNameIn, String prefixToRemoveIn) {
		StringBuilder sb = new StringBuilder();
		String[] str = tableNameIn.split("_");
		
		String prefix="";
		if (prefixToRemoveIn != null && prefixToRemoveIn.trim().length() > 0) {
			prefix = prefixToRemoveIn.replace("_", "");
		}
		
		for (String temp : str) {
			if (prefix.trim().length() > 0 && temp.equals(prefix)) {
				continue; // do not append
			} else {
				sb.append(Character.toUpperCase(temp.charAt(0)));
				sb.append(temp.substring(1).toLowerCase());
			}
		}
		
		return sb.toString();
	}
	
	public static String getTodaysDateyyyyMMdd() {
		//new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime())
		return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	}
	public static String getTodaysDateMMddYYYY() {
		//new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime())
		return new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
	}
	public static String getCurrentYear() {
		//new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime())
		return new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
	}
	
	/**
	 * Use:
	 * 
	 * If I pass in the tag string below
	 * <<columnLoop templateDirectory="searchParameters" separator=",">> 
	 * and an attribute of templateDirectory
	 * then I should get back a string of:
	 * "searchParameters"
	 * 
	 * @param tagIn
	 * @return
	 */
	public static String getAttributeValue(String tagIn, String attributeIn) {
		String tempValue = "";
		if (tagIn != null && tagIn.length() >0 && attributeIn != null && attributeIn.length() > 0) {
			int startPos = tagIn.indexOf(attributeIn);
			if (startPos >= 0) {
				int startPosValue = tagIn.indexOf("\"", startPos);
				int endPosValue = tagIn.indexOf("\"", startPosValue+1);
				if (startPosValue > 0 && endPosValue > 0) {
					tempValue = tagIn.substring(startPosValue+1, endPosValue);
				}
			}
		}
		
		return tempValue;
	}
	
	/**
	 *  Returns back a new string without the tag and its contents.
	 *  
	 *  Not sure how useful this will be, since I really want to replace the offending string with the actual contents...
	 *  
	 * @param bufferIn A big string of text
	 * @param tagIn a tag, like columnLoop
	 * @param tagDelimiterIn an opening delimiter like <<
	 * @param closingTagDelimiterIn a closing delimiter like >>
	 * @return
	 */
	public static String removeTag(String bufferIn, String tagIn, String tagDelimiterIn, String closingTagDelimiterIn) {
		String newBuffer = bufferIn;
		if (bufferIn != null && bufferIn.length() > 0 && tagIn != null && tagIn.length() > 0 && 
			tagDelimiterIn != null && tagDelimiterIn.length() > 0 && closingTagDelimiterIn != null && closingTagDelimiterIn.length() > 0) {
			int startTagPos = bufferIn.indexOf(tagDelimiterIn+tagIn);
			//System.out.println("startTagPos:"+startTagPos);
			if (startTagPos >= 0) {
				int endTagPos = bufferIn.indexOf(closingTagDelimiterIn, startTagPos+tagIn.length());
				//System.out.println("endTagPos:"+endTagPos);
				if (startTagPos == 0) {
					newBuffer = bufferIn.substring(endTagPos+closingTagDelimiterIn.length());
				} else {
					newBuffer = bufferIn.substring(0, startTagPos) + bufferIn.substring(endTagPos+closingTagDelimiterIn.length());
				}
			} else {
				; // tag and delimiter not found; return back initial string
			}
		}
		
		return newBuffer;
	}
	
	/**
	 * Ideally this will extract a copy of the  <<columnLoop xxxxx >> tag from the buffer
	 * @param bufferIn
	 * @param tagIn
	 * @param tagDelimiterIn
	 * @param closingTagDelimiterIn
	 * @return
	 */
	public static String getTag(String bufferIn, String tagIn, String tagDelimiterIn, String closingTagDelimiterIn) {
		String newBuffer = bufferIn;
		if (bufferIn != null && bufferIn.length() > 0 && tagIn != null && tagIn.length() > 0 && 
			tagDelimiterIn != null && tagDelimiterIn.length() > 0 && closingTagDelimiterIn != null && closingTagDelimiterIn.length() > 0) {
			int startTagPos = bufferIn.indexOf(tagDelimiterIn+tagIn);
			//System.out.println("startTagPos:"+startTagPos);
			if (startTagPos >= 0) {
				int endTagPos = bufferIn.indexOf(closingTagDelimiterIn, startTagPos+tagIn.length());
				//System.out.println("endTagPos:"+endTagPos);
				if (startTagPos == 0) {
					newBuffer = bufferIn.substring(0,endTagPos+closingTagDelimiterIn.length());
				} else {
					newBuffer = bufferIn.substring(startTagPos,endTagPos+closingTagDelimiterIn.length());
				}
			} else {
				; // tag and delimiter not found; return back initial string
			}
		}
		
		return newBuffer;
	}

}
