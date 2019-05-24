package org.foundations.app.generate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.foundations.app.model.ColumnModel;
import org.foundations.app.model.TableModel;

public class BuildTableModel extends BaseFoundation{
	
	//private String author = null;
	private String schema = null;
	
	//public static final String DEFAULT_AUTHOR = "unknown";
	
	TableModel tableModel = null;

	//	public BuildTableModel(Connection connectionIn, String authorIn, String schemaIn) {
	public BuildTableModel(Connection connectionIn,String schemaIn) {
		connection = connectionIn;
//		if (authorIn != null && authorIn.length() > 0) {
//			author = authorIn;
//		} else {
//			author = DEFAULT_AUTHOR;
//		}
		
		if (schemaIn != null && schemaIn.length() > 0) {
			schema = schemaIn;
		}
	}
	
	public TableModel buildTableSpec(String tableNameIn) {
		logStatus("buildTableSpec","Called for TBL:["+tableNameIn+"]");
		tableModel = new TableModel();
		tableModel.setTableName(tableNameIn);
		tableModel.setSchema(schema);
		tableModel.setColumnModel(buildColumns(tableNameIn));
		tableModel.setPrimaryKeyColumnsList(buildPrimaryKey(tableNameIn));
		tableModel.setPrimaryKeyColumnModel(buildPrimaryKeyColumnModels());
		//TODO build unique key information
		//buildUniqueKey("EL_COLLECTIVE_COMPAT");
		//buildUniqueKey(tableNameIn);
		logStatus("buildTableSpec",tableModel.toString());
		return tableModel;
	}
	
	private List<String> buildPrimaryKey(String tableNameIn) {
		logStatus("buildPrimaryKey","Called for TBL:["+tableNameIn+"]");
		StringBuilder builder = new StringBuilder();
		
		List<String> primaryKeyColumnsList = new ArrayList<>();

		DatabaseMetaData metaData;
		try {
			metaData = connection.getMetaData();
			
			// below works for Oracle, not postgresql
			// try (ResultSet tables = metaData.getTables(null, null, tableNameIn, new String[]{"TABLE"})) {
			
			// line below did not work in postgresql either...
			// try (ResultSet tables = metaData.getTables(null, null, tableNameIn, new String[]{"table"})) {
	
			try (ResultSet tables = metaData.getTables(null, null, tableNameIn, new String[]{"TABLE"})) {

				while (tables.next()) {
					String catalog = tables.getString("TABLE_CAT");
					logStatus("buildPrimaryKey", "catalog is: "+catalog+" for database tables (should be null)");
					String schema = tables.getString("TABLE_SCHEM");
					String tableName = tables.getString("TABLE_NAME");
					try(ResultSet primaryKeys = metaData.getPrimaryKeys(catalog, schema, tableName)) {
						while (primaryKeys.next()) {
							primaryKeyColumnsList.add(primaryKeys.getString("COLUMN_NAME"));
							System.out.println("BuildTableModel.buildPrimaryKey()Primary Key: "+ primaryKeys.getString("COLUMN_NAME"));
						}
					}
				}
				
			} catch (SQLException e) {
				logError("buildPrimaryKey","Problem encountered identifying the primary key component(s) for table "+ tableNameIn+". Exception:"+e.getMessage());
			}
			
		} catch (SQLException sqe) {
			logError("buildPrimaryKey","Problem encountered getting database meta data while trying to build objects for "+ tableNameIn+". Exception:"+sqe.getMessage());
		}	
		return primaryKeyColumnsList;
	}
	
	public List<ColumnModel> buildPrimaryKeyColumnModels() {
		List<ColumnModel> pkList = new ArrayList<>();

		for (String pkColumnName : tableModel.getPrimaryKeyColumnsList()) {
			ColumnModel pkColumn = getColumnModelForColumnName(pkColumnName);
			pkList.add(pkColumn);
			
		}
		
		return pkList;
	}
	
	public ColumnModel getColumnModelForColumnName(String columnNameIn) {
		ColumnModel tempColumnModel = null;
		
		if (tableModel.getColumnModel() != null ) {
			for (ColumnModel currentLoopColumnModel : tableModel.getColumnModel()) {
				if (currentLoopColumnModel.getColumnName().equals(columnNameIn)) {
					tempColumnModel = currentLoopColumnModel;
					break;
				}
			}
		}
		
		return tempColumnModel;
	}
	
	/**
	 * FAILS - Do not have proper permissions in Oracle (ORA-01031)
	 * 
	 * @param tableNameIn
	 * @return
	 */
	private List<String> buildUniqueKey(String tableNameIn) {
		String methodName = "buildUniqueKey";
		logStatus(methodName,"Called for TBL:["+tableNameIn+"]");
		logStatus(methodName,"THIS METHOD DOES NOT SEEM TO BE FUNCTIONING PROPERLY - PERMISSIONS ISSUE (ORA-01031) !!!");
		
		List<String> keyColumnsList = new ArrayList<>();

		DatabaseMetaData metaData;
		try {
			metaData = connection.getMetaData();
	
			try (ResultSet tables = metaData.getTables(null, null, tableNameIn, new String[]{"TABLE"})) {

				while (tables.next()) {
					String catalog = tables.getString("TABLE_CAT");
					logStatus(methodName, "catalog is: "+catalog+" for database tables (should be null)");
					String schema = tables.getString("TABLE_SCHEM");
					String tableName = tables.getString("TABLE_NAME");
					logStatus(methodName, "tableName is: "+tableName+" which should match:"+tableNameIn);
					//					try(ResultSet primaryKeys = metaData.getPrimaryKeys(catalog, schema, tableName)) {
					//					try(ResultSet keys = metaData.getImportedKeys(catalog, schema, tableName)) {
					try(ResultSet keys = metaData.getIndexInfo(catalog, schema, tableName, true, false)) {
						while (keys.next()) {
							logStatus(methodName,"keys found for:["+tableNameIn+"]");
							keyColumnsList.add(keys.getString("COLUMN_NAME"));
							System.out.println(methodName+ " Unique Key: "+ keys.getString("COLUMN_NAME"));
						}
					}
				}
				
			} catch (SQLException e) {
				logError(methodName,"Problem encountered identifying the unique key component(s) for table "+ tableNameIn+". Exception:"+e.getMessage());
			}
			
		} catch (SQLException sqe) {
			logError(methodName,"Problem encountered getting database meta data while trying to build objects for "+ tableNameIn+". Exception:"+sqe.getMessage());
		}	
		return keyColumnsList;
	}
	
	public List<ColumnModel> buildColumns(String tableNameIn) {
		//StringBuilder builder = new StringBuilder();
		List<ColumnModel> columns = new ArrayList<>();
		logStatus("buildColumns","Called for TBL:["+tableNameIn+"]");
		logProgress("buildColumns","Called for TBL:["+tableNameIn+"]");
		
		String sql = "select * from " + tableNameIn + " where 1 < 0 ";
		
		
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i<=columnCount; i++) {
				ColumnModel column = new ColumnModel();
				column.setColumnName(rsmd.getColumnName(i));
				column.setDataType(rsmd.getColumnTypeName(i));
				column.setColumnType(rsmd.getColumnType(i));
				column.setDataLength(rsmd.getColumnDisplaySize(i));
				column.setDataPrecision(rsmd.getPrecision(i));
				column.setDataScale(rsmd.getScale(i));
				logStatus("buildColumns", column.toString());
				columns.add(column);
				//logProgress("buildHelper", tableNameIn+" "+columnName+" "+dataType+"("+dataLength+")"+ "SQLCOLTYPE:"+columnType+"");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return columns;
	}

}
