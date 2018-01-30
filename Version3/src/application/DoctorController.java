package application;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import application.Helper.DBCaller;
import application.Model.DeviceConfiguration;
import application.Model.PatientHistory;
import application.Model.PatientProfile;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DoctorController  implements  ControlledScreen  {
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
	 @FXML	 Button btnGraphView = new Button();
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
	 @FXML CategoryAxis Time = new CategoryAxis();
	 @FXML	NumberAxis GlucoseLevel = new NumberAxis(); 
	 @FXML	LineChart<String,Number> HistoryChart = new LineChart<>(Time, GlucoseLevel);
	 @FXML Hyperlink hyperlink ;
	 @FXML private Button goBack;
	 private HostServices hostServices ;

	    
	 ScreensController myController;
	// @FXML final CategoryAxis xAxis = new CategoryAxis();
	// @FXML final NumberAxis yAxis = new NumberAxis();  
	// @FXML  LineChart<String,Number> lineChart;
	 private final Main main = new Main() ;
	 @FXML  private void initialize()
	 {
		 hyperlink.setOnAction(e -> main.getHostServices().showDocument(hyperlink.getText()));
			cmbPatientID.setItems(DBCaller.selectPatientID("AllPatient"));
	        // Initialize the person table with the three columns.
			phDate.setCellValueFactory( x -> x.getValue().getDateOfTheReading());
			phTime.setCellValueFactory( x -> x.getValue().getTimeOfTheReading());
			phGlucoseLevel.setCellValueFactory( x -> x.getValue().getGlucoseValue().asObject());
	 }
	 public void goBack(ActionEvent event) throws IOException  {
			myController.setScreen(Main.screen4ID);
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
	if( DBCaller.InsertNewPatient(NewPatient)==1 )	
	  {
		NewDevice.setManual(ManualMode.isSelected());
		NewDevice.setNextAppointment(dtNextAppointment.getValue().toString());
		NewDevice.setPatientID(DBCaller.selectPatientID("LastID").get(0));
		if(DBCaller.InsertNewDeviceConfiguration(NewDevice)==1)
		{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("New User");
			alert.setHeaderText("New User Added Successfully!!");	
			alert.showAndWait();
			cmbPatientID.setItems(DBCaller.selectPatientID("AllPatient"));	
		}
		
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
		HistoryGraph();
	}
	
	public void SaveDeviceConfiguration() {
		System.out.println(dtHistoryDate.getValue().toString());
		patientHistory = DBCaller.selectPatientHistory("Date", dtHistoryDate.getValue().toString(), cmbPatientID.getValue());
		tableView.setItems(patientHistory);
	}
	
	
	public void HistoryGraph()
	{
		if(HistoryChart.getData().size()> 0)
		{
			 HistoryChart.getData().remove(0);
		}
		if(patientHistory.size()>0) {		
		//Dialog dialog = new Dialog();
		//dialog.setTitle("Test");		
		//dialog.setResizable(true);
		
		Time.setLabel("Time Of the Day");  
		GlucoseLevel.setLabel("Glucose mg/dL");  
		HistoryChart.setTitle("Glucose Variation Graph");
		HistoryChart.setCreateSymbols(false);
	    Series<String, Number> series = new XYChart.Series<>();
	    series.setName("Glucose Variation");
	   
for (PatientHistory History : patientHistory) {

	 series.getData().add(new Data<String, Number>(History.getTimeOfTheReading().getValue(), History.getGlucoseValue().getValue()));
}
	System.out.println(series.getData().size());
	if(series.getData().size()>0)
		HistoryChart.getData().add(series);
	
	//dialog.getDialogPane().setContent(HistoryChart);
	//ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
	//dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
	//dialog.showAndWait();      
	        
		}    
	}

	@Override
	public void setScreenParent(ScreensController screenPage) {
		myController =screenPage;
		
	}
	
	

    
    public void openURL() {
    	System.out.println(hyperlink.getText());
    	 Platform.runLater(() -> {
    		 hostServices.showDocument("www.google.com");
    		 //hostServices.showDocument(hyperlink.getText());
            });
			
       
    }
	}
	
	

