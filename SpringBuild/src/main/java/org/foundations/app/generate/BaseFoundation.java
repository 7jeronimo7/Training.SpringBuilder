package org.foundations.app.generate;

import java.io.File;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.foundations.app.model.UserSpecificationsModel;

public class BaseFoundation {
	
	public static final String APP_CODE = "SpringFoundations";
	
	public static final String TAG_OPEN_DELIMIT = "<<";
	public static final String TAG_CLOSE_DELIMIT = ">>";
	
	public static final String ERROR_TYPE = "ERROR";
	public static final String INFO_TYPE = "INFO";
	public static final String WARN_TYPE = "WARN";
	public static final String DEBUG_TYPE = "DEBUG";
	public static final String AUDIT_TYPE = "AUDIT";
	
	public static final String NODE_NAME = "N/A";
	
	Connection connection = null;
	
	UserSpecificationsModel userSpecs = null;
	
	protected String getResourcesDirectory() {
		return System.getProperty("user.dir")+File.separator+"build"+File.separator+"resources"+File.separator+"main"+File.separator;
	}
	
	protected String getLegacyTemplatesDirectory() {
		String directory = System.getProperty("user.dir")+File.separator+"build"+File.separator+"resources"+
	                       File.separator+"main"+File.separator+"templates"+File.separator+"legacy"+File.separator;
		return directory;
	}
	
	protected String getSpringTemplatesDirectory() {
		String directory = System.getProperty("user.dir")+File.separator+"build"+File.separator+"resources"+
	                       File.separator+"main"+File.separator+"templates"+File.separator+"spring"+File.separator;
		return directory;
	}
	
	protected void logStatus(String methodIn, String msgIn) {
		System.out.println("###" + methodIn+"### " + msgIn+"\n");
	}
	
	protected void logProgress(String methodIn, String msgIn) {
		logEvent(methodIn, getClass().getSimpleName(), AUDIT_TYPE, msgIn);
	}
	
	protected void logError(String methodIn, String msgIn) {
		logEvent(methodIn, getClass().getSimpleName(), ERROR_TYPE, msgIn);
	}
	
	protected void logWarning(String methodIn, String msgIn) {
		logEvent(methodIn, getClass().getSimpleName(), WARN_TYPE, msgIn);
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
//				logStatus("logEvent", "FAILED TO RECORD MESSAGE:["+msgIn+"] "+ e.getMessage());
//				//logStatus(methodIn, msgIn);
//				e.printStackTrace();
//			}
//		} else {
//			logStatus(methodIn, msgIn);
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
