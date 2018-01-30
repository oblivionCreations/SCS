package application.Helper;



import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.ControlledScreen;
import application.Main;
import application.ScreensController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

//import javafx.fxml.FXML;

public class HelperController implements Initializable, ControlledScreen {
	
	public HelperLoginModel loginModal = new HelperLoginModel();
	@FXML
	private TextField txtUserName,txtPassword;
	@FXML
	private Label isConnected;
	@FXML
	private Button Login,goBack;
	
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
			if(loginModal.isLogin(txtUserName.getText(), txtPassword.getText())) {
				isConnected.setText("User name & Password is correct");
				myController.setScreen(Main.screen8ID);
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

