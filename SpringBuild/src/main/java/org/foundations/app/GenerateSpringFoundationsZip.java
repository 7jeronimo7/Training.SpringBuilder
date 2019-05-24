package org.foundations.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.foundations.app.generate.BuildJava;
import org.foundations.app.generate.BuildTableModel;
import org.foundations.app.model.TableModel;
import org.foundations.app.model.UserSpecificationsModel;
import org.foundations.app.util.StringUtilities;

// 02/11/2019 - I probably want to rename this class
public class GenerateSpringFoundationsZip {
	
	//tempFileName.append("C:/temp/");
	//tempFileName.append("foundations_");
	private static final String JUNIT_OUTPUT_ZIP_DIR = "C:/TempSXH/Foundations/SpringFoundations/";
	
	public static final String APP_CODE = "SpringFoundations";
	public static final String NODE_NAME = "N/A";
	
	public static final String NO_OBJECT_PREFIX = "";
	public static final String NO_OBJECT_SUFFIX = "";
	
	public static final String JAVA_FILETYPE = ".java";
	public static final String JSP_FILETYPE = ".jsp";
	
	private final static String SQL_DB_DRIVER_NAME_PROPERTY = "bondworlddb.db.JDBCClassDriverName";
	private final static String SQL_DB_URL_PROPERTY 		= "bondworlddb.db.url";
	private final static String SQL_DB_ID_PROPERTY 			= "bondworlddb.db.id";
	private final static String SQL_DB_PASSWORD_PROPERTY 	= "bondworlddb.db.password";
	
	private final static String PROP_FILE_NAME = "db.properties";
	
	public Connection connection = null;
	protected UserSpecificationsModel userSpecs = null;
	
	public String buildZip(String schemaIn, String tableNameIn, String authorIn, String pkgNameIn, String typeIn, String versionIn) {
		String javaCode ="";
		StringBuilder builder = new StringBuilder();

		initDBConnection();
		
		if (connection != null) {
			buildProducts(schemaIn, tableNameIn, authorIn, pkgNameIn, typeIn, versionIn);
			javaCode ="SUCCESS"; //TODO remove
			try {
				connection.close();
			} catch (SQLException ignore) {	; /* Do nothing */ }
		}
		
		
		return javaCode;
	}
	
	private void initDBConnection() {

		String sqlDriver;
		String sqlUrl;
		String sqlId;
		String sqlPassword;
		
		System.out.println(">> check for file under:"+System.getProperty("user.dir")+"\n");
// 		try (FileInputStream fis = new FileInputStream(File.pathSeparator+"build"+File.pathSeparator+"resources"+File.pathSeparator+"main"+File.pathSeparator+PROP_FILE_NAME)) {

		try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+File.separator+"build"+File.separator+"resources"+File.separator+"main"+File.separator+PROP_FILE_NAME)) {
			
			Properties properties = new Properties();
			properties.load(fis); //TODO I could make this global to store additional values...
			
			sqlDriver = properties.getProperty(SQL_DB_DRIVER_NAME_PROPERTY);
			sqlUrl = properties.getProperty(SQL_DB_URL_PROPERTY);
			sqlId = properties.getProperty(SQL_DB_ID_PROPERTY);
			sqlPassword = properties.getProperty(SQL_DB_PASSWORD_PROPERTY);
			//	Load driver and connect to db
			Class.forName(sqlDriver);
			connection = DriverManager.getConnection(sqlUrl, sqlId, sqlPassword);
			
		} catch (FileNotFoundException e) {
			// could not find the properties file
			e.printStackTrace();
		} catch (IOException e) {
			// problem opening properties file
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// driver class not found
			e.printStackTrace();
		} catch (SQLException e) {
			// problems connecting to the database
			e.printStackTrace();
		}


	}
	
	//TODO remove parameters and make global
	private StringBuilder buildProducts(String schemaIn, String tableNameIn, String authorIn, String pkgNameIn, String typeIn, String versionIn) {
		String processName = "buildProducts";
		ZipOutputStream myZip = null;
		//ByteArrayOutputStream baStream = null;
		StringBuilder builder = new StringBuilder();
		StringBuilder readMeBuilder = new StringBuilder();

		userSpecs = new UserSpecificationsModel();
		userSpecs.setAuthor(authorIn);
		userSpecs.setCopyright("Spring Foundations");
		userSpecs.setModLogJobId("SPF-0001");
		userSpecs.setPackageName(pkgNameIn);
		userSpecs.setVersion(versionIn);
		
		BuildTableModel tableSpecBuilder = new BuildTableModel(connection, schemaIn);
		TableModel tableModel = tableSpecBuilder.buildTableSpec(tableNameIn);
		
		try ( ByteArrayOutputStream baStream = new ByteArrayOutputStream() ) {
			
			myZip = new ZipOutputStream(baStream);
			
			readMeBuilder.append("README.TXT - Contents\r\n");
			readMeBuilder.append("\n\n Objects generated for: "+ tableNameIn);
			readMeBuilder.append("\r\n on:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
			readMeBuilder.append("\r\n");
			
			buildNewObject(myZip, tableModel, NO_OBJECT_PREFIX, "DAOImpl", "DAOObjectImpl", JAVA_FILETYPE);
			readMeBuilder.append(""+StringUtilities.convertTableNameToClassName(tableModel.getTableName())+"DAOImpl.java\r\n");
			
			buildNewObject(myZip, tableModel, NO_OBJECT_PREFIX, "DAO", "DAOObject", JAVA_FILETYPE);
			readMeBuilder.append(""+StringUtilities.convertTableNameToClassName(tableModel.getTableName())+"DAO.java\r\n");
			
			buildNewObject(myZip, tableModel, NO_OBJECT_PREFIX, NO_OBJECT_SUFFIX, "Object", JAVA_FILETYPE);
			readMeBuilder.append(""+StringUtilities.convertTableNameToClassName(tableModel.getTableName())+".java\r\n");
			
			buildNewObject(myZip, tableModel, NO_OBJECT_PREFIX, "Controller", "ObjectController", JAVA_FILETYPE);
			readMeBuilder.append(""+StringUtilities.convertTableNameToClassName(tableModel.getTableName())+"Controller.java\r\n");
			
			buildNewObject(myZip, tableModel, "list-", NO_OBJECT_SUFFIX, "ListObjectJSP", JSP_FILETYPE);
			readMeBuilder.append("list-"+StringUtilities.convertToCamelCase(tableModel.getTableName())+".jsp\r\n");
		
			zipReadme(myZip, readMeBuilder);
			
			myZip.flush();
			//myZip.closeEntry(); //added 17-feb-2019 (seems redundant!!!)
			myZip.close();

			createZipFlatFile(baStream); //TODO remove; for testing only!!!
			
		} catch (IOException ioe) {
			logEvent(processName, getClass().getSimpleName(), "ERROR", "Problem creating zip output streams: "+ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			if (myZip != null) {
				try {
					myZip.close(); // does not implement autoclose...
				} catch (IOException ignore) {
					; // do nothing
				}
			}
		}
		
		return builder;
	}
	
	private void buildNewObject(ZipOutputStream myZip, TableModel tableModel, String objectPrefix, String objectSuffix, String templateIn, String fileExtensionIn) {
		String processName = "buildNewObject";
		BuildJava builder = new BuildJava(connection, userSpecs, templateIn);
		String codeForFile = builder.buildJavaObjectForTable(tableModel);
		
		ZipEntry myZE = null;
		String objectFileName =objectPrefix+StringUtilities.convertTableNameToClassName(tableModel.getTableName())+objectSuffix+fileExtensionIn;
		logEvent(processName, getClass().getSimpleName(),"AUDIT","Zipping:"+objectFileName);
		myZE = new ZipEntry(objectFileName);
		try {
			myZip.putNextEntry(myZE);
			myZip.write(codeForFile.getBytes());
			myZip.closeEntry();
			
		} catch (IOException e) {
			logEvent(processName, getClass().getSimpleName(), "ERROR", "Problem generating zip entry:" +e.toString());
			e.printStackTrace();
		
		}
		
	}

	

	
	/**
	 * zip file is created, but it is corrupt.  7zip shows the readme file and a content length, but again, it
	 * cannot extract the file.  Perhaps I should remove the try-with-resources blocks and manually close resources...
	 * 
	 * @param baStream
	 */
	private void createZipFlatFile(ByteArrayOutputStream baStream) {
		String processName = "createZipFlatFile";
		//try (FileOutputStream fos = new FileOutputStream("C:/temp/my_super_test_foundations.zip")) {
		try (FileOutputStream fos = new FileOutputStream(generateFlatFilename())) {
			baStream.writeTo(fos);
			baStream.close(); //auto-close with do this, but I want to close this before the fos is closed...
		} catch (FileNotFoundException fnfe) {
			logEvent(processName, getClass().getSimpleName(), "ERROR", "Directory or File not found for output:"+fnfe.getMessage());
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			logEvent(processName, getClass().getSimpleName(), "ERROR", "Problem creating zip flat file for testing purposes"+ioe.getMessage());
			ioe.printStackTrace();
		}
	}
	
	/**
	 * At this point, only used for debugging with JUnit...
	 * @return
	 */
	private String generateFlatFilename() {
		StringBuilder tempFileName = new StringBuilder();
		tempFileName.append(JUNIT_OUTPUT_ZIP_DIR);
		tempFileName.append("foundations_");
		tempFileName.append(new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime()));
		tempFileName.append(".zip");
		
		return tempFileName.toString();
	}
	
	
	
	private void zipReadme(ZipOutputStream myZip, StringBuilder readMe) {
		String processName = "zipReadme";
		ZipEntry myZE = null;
		myZE = new ZipEntry("README.txt");
		try {
			myZip.putNextEntry(myZE);
			myZip.write(readMe.toString().getBytes());
			myZip.closeEntry();
			
		} catch (IOException e) {
			logEvent(processName, getClass().getSimpleName(), "ERROR", "Problem generating zip entry:" +e.toString());
			e.printStackTrace();
		}
	}
	
	protected void logEvent(String methodIn, String moduleIn, String msgTypeIn, String msgIn) {
		
		System.out.println(">>>"+moduleIn+" ["+ msgTypeIn+"]  "+ msgIn);
//		String sql = "DECLARE "+
//				  "BEGIN " +
//				  " edp_msg.log_eventR(?,?,?,?,?,?,?,?,?); " +
//				  "END;";
//		if (connection != null ) {
//			try (CallableStatement sqlProc = connection.prepareCall(sql)) {
//				sqlProc.setString(1, APP_CODE);
//				sqlProc.setString(2, methodIn);
//				sqlProc.setString(3, msgTypeIn);
//				sqlProc.setString(4, msgIn);
//				sqlProc.setString(5, null);
//				sqlProc.setString(6, null);
//				sqlProc.setString(7, null);
//				sqlProc.setString(8, getNodeName());
//				sqlProc.setString(9, moduleIn);
//				sqlProc.execute();
//			} catch (SQLException e) {
//				//logStatus("logEvent", "FAILED TO RECORD MESSAGE:["+msgIn+"] "+ e.getMessage());
//				//logStatus(methodIn, msgIn);
//				e.printStackTrace();
//			}
//		} else {
//			//logStatus(methodIn, msgIn);
//		}
	}
	
	public String getNodeName() {
		String nodeName;
		try {
			nodeName = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			nodeName = NODE_NAME;
		}
		return nodeName;
	}

}
