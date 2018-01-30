package application.Mapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import application.Model.PatientHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Mapper {

	public static ObservableList<PatientHistory> PatientHistoryMapper(ResultSet rs) throws SQLException {
		ObservableList<PatientHistory> patientHistory = FXCollections.observableArrayList();
		 while (rs.next()) {             
			 patientHistory.add(new PatientHistory(rs.getString("DateOfReading"), rs.getString("TimeOfReading"), rs.getDouble("GlucoseLevel"),rs.getInt("PatientID")));
		 }         
		return patientHistory;
	}
	
	
	public static ObservableList<Integer> PatientIDMapper(ResultSet rs) throws SQLException {
		ObservableList<Integer> patientID = FXCollections.observableArrayList();
		 while (rs.next()) {             
			 patientID.add(rs.getInt("PatientID"));
		 }
         
		return patientID;
	}
}
