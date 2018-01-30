package application.Helper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import application.Mapper.Mapper;
import application.Model.DeviceConfiguration;
import application.Model.PatientDetails;
import application.Model.PatientHistory;
import application.Model.PatientProfile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class DBCaller {
	
	 public static ObservableList<PatientHistory> selectPatientHistory(String type,String Date,int PatientID){
		 String sql ="" ;
		 System.out.println(type);
		 switch(type)
		 {
		 case("All"):sql ="SELECT * FROM PatientHistory where PatientID ="+PatientID;break;
		 case("Today"): sql ="SELECT * FROM PatientHistory where DateOfReading= date('now') and PatientID ="+PatientID;break;
		 case("Date"): sql ="SELECT * FROM PatientHistory where DateOfReading= '"+Date+"' and PatientID ="+PatientID;break;
		 case("PatientID"): sql ="SELECT DISTINCT PatientID FROM PatientHistory";break;
		 }
		 	System.out.println(sql);
		 
	        ObservableList<PatientHistory> history = FXCollections.observableArrayList();
	        try{
	        	Connection conn = SQLLiteConnection.Connecter();
	             Statement stmt  = conn.createStatement();
	             ResultSet rs    = stmt.executeQuery(sql);
	             history =  Mapper.PatientHistoryMapper(rs);	             
	           }
	        catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        return history;
	    }
	 
	 
	 public static PatientProfile selectPatientProfile(String type,int patientID){
		 String sql ="" ;
		 System.out.println(type);
		 switch(type)
		 {		
		 case("Current"): sql ="SELECT * FROM PatientProfile where  PatientID ="+patientID;break;
		 }			 
		 	PatientProfile patientProfile= new PatientProfile();
	        try
	        {
	        	 Connection conn = SQLLiteConnection.Connecter();
	             Statement stmt  = conn.createStatement();
	             ResultSet rs    = stmt.executeQuery(sql);
	             patientProfile.setName(rs.getString("Name"));
	             patientProfile.setAddress(rs.getString("Address"));
	             patientProfile.setAge(rs.getInt("Age"));
	             patientProfile.setAdditionalDrugs(rs.getString("AdditionalDrugs"));
	             patientProfile.setPatientID(patientID);
	             patientProfile.setContactNo(rs.getString("ContactNo"));
	        }
	        catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        return patientProfile;
	    }
	 
	 public static ObservableList<Integer> selectPatientID(){
		 ObservableList<Integer> PatientIDs = FXCollections.observableArrayList();
		 	String  sql ="SELECT DISTINCT PatientID FROM PatientProfile";
	        try
	        {
	        	 Connection conn = SQLLiteConnection.Connecter();
	             Statement stmt  = conn.createStatement();
	             ResultSet rs    = stmt.executeQuery(sql);
	             PatientIDs =  Mapper.PatientIDMapper(rs);	             
	        }
	        catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        return PatientIDs;
	 }
	 
	 public static int InsertNewPatient(PatientProfile patientProfile ){
		 
		 String sql = "INSERT INTO PatientProfile(name,Address,Age,AdditionalDrugs,ContactNo) VALUES(?,?,?,?,?)";
		 int status = 2;
	        try (Connection conn = SQLLiteConnection.Connecter();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, patientProfile.getName());
	            pstmt.setString(2, patientProfile.getAddress());
	            pstmt.setInt(3, patientProfile.getAge());
	            pstmt.setString(4, patientProfile.getAdditionalDrugs());
	            pstmt.setString(5, patientProfile.getContactNo());
	          status =  pstmt.executeUpdate();
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        System.out.println(status);
	        return status;
	       
	 }
	 
	 
	 public static int InsertNewDeviceConfiguration(DeviceConfiguration deviceConfiguration ){
		 
		 String sql = "INSERT INTO DeviceConfiguration(IsManual,NextAppointment,PatientID) VALUES(?,?,?)";
		 int status = 2;
	        try (Connection conn = SQLLiteConnection.Connecter();
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setBoolean(1, deviceConfiguration.isManual());
	            pstmt.setString(2, deviceConfiguration.getNextAppointment());
	            pstmt.setInt(3, deviceConfiguration.getPatientID());	           
	          status =  pstmt.executeUpdate();
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        System.out.println(status);
	        return status;
	       
	 }
	 
	 

	 
	 
 public static DeviceConfiguration GetPatientDetails(int PatientID ){
		 
		 String sql = "SELECT * FROM DeviceConfiguration where  PatientID ="+PatientID;
		 DeviceConfiguration deviceConfiguration= new DeviceConfiguration();
	        try
	        {
	        	 Connection conn = SQLLiteConnection.Connecter();
	             Statement stmt  = conn.createStatement();
	             ResultSet rs    = stmt.executeQuery(sql);
	             deviceConfiguration.setManual(rs.getBoolean("isManual"));
	             deviceConfiguration.setNextAppointment(rs.getString("NextAppointment"));
	         }
	        catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        return deviceConfiguration;
	      	 }
}
