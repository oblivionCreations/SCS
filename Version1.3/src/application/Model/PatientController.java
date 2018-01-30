package application.Model;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.ControlledScreen;
import application.Main;
import application.ScreensController;
import application.Helper.DBCaller;
import application.Model.PatientLoginModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

//import javafx.fxml.FXML;

public class PatientController implements Initializable, ControlledScreen {
	
	public PatientLoginModel loginModal = new PatientLoginModel();
	@FXML	private TextField txtUserName,txtPassword;
	@FXML	private Label isConnected;
	@FXML	private Button Login,goBack;

	public static int PatientID = 0;
	ScreensController myController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(loginModal.isDBConnected()) {
			isConnected.setText("Connected");
		}
		else
		{
			isConnected.setText("Not Connected");
		}
		
	}
	public void login(ActionEvent event) {
		try {
			if(loginModal.isLogin(Integer.parseInt(txtUserName.getText()), txtPassword.getText())) {
				isConnected.setText("User name & Password is correct");
		        PatientID = Integer.parseInt(txtUserName.getText()) ;
				
				DeviceConfiguration deviceConfiguration =  DBCaller.checkPatientMode(PatientController.PatientID);
				if(!deviceConfiguration.isManual()) {
					myController.setScreen(Main.screen6ID);
				}else {
					myController.setScreen(Main.screen7ID);
				}
							
			}
			else {
				isConnected.setText("User name & Password is incorrect");
				
			}
		} catch (SQLException e) {
			isConnected.setText("User name & Password is incorrect");
			
		}
		}
	
	public void goBack(ActionEvent event) throws IOException  {
		myController.setScreen(Main.screen1ID);
	}
	@Override
	public void setScreenParent(ScreensController screenPage) {
		// TODO Auto-generated method stub
		myController =screenPage;
	}
	

}
