package application;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import application.Helper.DBCaller;
import application.Model.DeviceConfiguration;
import application.Model.PatientDetails;
import application.Model.PatientHistory;
import application.Model.PatientProfile;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DoctorController   {
	DeviceConfiguration deviceConfiguration = new DeviceConfiguration();
	List<PatientProfile> patientList = new ArrayList<PatientProfile>();
	ObservableList<PatientHistory> patientHistory = FXCollections.observableArrayList();
	ObservableList<Integer> IDList = FXCollections.observableArrayList();	
	 @FXML	 TextField txtpatientName = new TextField(); 
	 @FXML	TextField txtpatientAge = new TextField();
	 @FXML	 TextField txtpatientAddress = new TextField(); 
	 @FXML	TextField txtpatientAdditionalDrugs = new TextField();
	 @FXML	 ComboBox<Integer> cmbPatientID = new ComboBox<Integer>(); 
	 @FXML	TextField txtpatientContactNo = new TextField();
	 @FXML	 Button btnLoad = new Button();
	 @FXML	 Button btnSave = new Button();
	 @FXML	 Button btnEnableAddtion = new Button();
	 @FXML	 Button btnSaveNewPatient = new Button();
	 @FXML	 Button btnGetHistoryByDate = new Button();
	 @FXML	 Button btnEditDeviceConfiguration = new Button();
	 @FXML	 Button btnSaveDeviceConfiguration = new Button();
	 @FXML	 DatePicker dtHistoryDate = new DatePicker();
	 @FXML	 DatePicker dtNextAppointment = new DatePicker();
	 @FXML	 RadioButton ManualMode = new RadioButton();
	 @FXML	 TableView<PatientHistory> tableView;
	 @FXML	 TableColumn<PatientHistory, String> phDate;
	 @FXML	 TableColumn<PatientHistory, String> phTime;
	 @FXML	 TableColumn<PatientHistory, Double> phGlucoseLevel ;
	 @FXML	 TableColumn<PatientHistory, Double> phDosage;
	 @FXML	 TableColumn<PatientHistory, String> phCriticality;		 
	 @FXML  private void initialize()
	 {
			cmbPatientID.setItems(DBCaller.selectPatientID());
	        // Initialize the person table with the three columns.
			phDate.setCellValueFactory( x -> x.getValue().getDateOfTheReading());
			phTime.setCellValueFactory( x -> x.getValue().getTimeOfTheReading());
			phGlucoseLevel.setCellValueFactory( x -> x.getValue().getGlucoseValue().asObject());
	 }
	 
	public void LoadPatientProfile() 
	{
		    PatientProfile  patientProfile = DBCaller.selectPatientProfile("Current", cmbPatientID.getValue());
		    DeviceConfiguration deviceConfiguration =  DBCaller.GetPatientDetails(cmbPatientID.getValue());
			txtpatientName.setText(patientProfile.getName());
			txtpatientAge.setText(Integer.toString(patientProfile.getAge()));
			txtpatientAddress.setText(patientProfile.getAddress());
			txtpatientAdditionalDrugs.setText(patientProfile.getAdditionalDrugs());			
			txtpatientContactNo.setText(patientProfile.getContactNo());
			ManualMode.setSelected(deviceConfiguration.isManual());
			//dtHistoryDate.setValue(deviceConfiguration.getNextAppointment());
			ManualMode.setSelected(true);			
			txtpatientName.setEditable(false);
			txtpatientAge.setEditable(false);
			txtpatientAddress.setEditable(false);
			txtpatientAdditionalDrugs.setEditable(false);		
			txtpatientContactNo.setEditable(false);
			
	}
	
	public void btnAddNewPatient() {
		PatientProfile NewPatient = new PatientProfile();
		DeviceConfiguration NewDevice = new DeviceConfiguration();
		NewPatient.setName(txtpatientName.getText());
		NewPatient.setAge(Integer.parseInt(txtpatientAge.getText()));
		NewPatient.setAddress(txtpatientAddress.getText());
		NewPatient.setAdditionalDrugs(txtpatientAdditionalDrugs.getText());
		NewPatient.setContactNo(txtpatientContactNo.getText());
		
		NewDevice.setManual(ManualMode.isSelected());
		NewDevice.setNextAppointment(dtNextAppointment.getValue().toString());
		//NewDevice.setPatientID(Get);
		//deviceConfiguration =  
	if( DBCaller.InsertNewPatient(NewPatient)==1 && DBCaller.InsertNewDeviceConfiguration(NewDevice)==1)	
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("New User");
		alert.setHeaderText("New User Added Successfully!!");	
		alert.showAndWait();
		cmbPatientID.setItems(DBCaller.selectPatientID());	
	}	
	}
	
		
	public void OnPatientIDSelect()
	{
		LoadPatientProfile();
		patientHistory = DBCaller.selectPatientHistory("Today","",cmbPatientID.getValue());
		
		tableView.setItems(patientHistory);
		System.out.println(cmbPatientID.getValue());
		System.out.println(patientList.stream());
	}
	
	public void EnableAddtion() {
		txtpatientName.clear();
		txtpatientAge.clear();
		txtpatientAddress.clear();
		txtpatientAdditionalDrugs.clear();		
		txtpatientContactNo.clear();
		txtpatientName.setEditable(true);
		txtpatientAge.setEditable(true);
		txtpatientAddress.setEditable(true);
		txtpatientAdditionalDrugs.setEditable(true);		
		txtpatientContactNo.setEditable(true);
	}
	
	public void GetHistoryByDate() {
		System.out.println(dtHistoryDate.getValue().toString());
		patientHistory = DBCaller.selectPatientHistory("Date", dtHistoryDate.getValue().toString(), cmbPatientID.getValue());
		tableView.setItems(patientHistory);
	}
	
	public void SaveDeviceConfiguration() {
		System.out.println(dtHistoryDate.getValue().toString());
		patientHistory = DBCaller.selectPatientHistory("Date", dtHistoryDate.getValue().toString(), cmbPatientID.getValue());
		tableView.setItems(patientHistory);
	}
	
	
	}
	
	

