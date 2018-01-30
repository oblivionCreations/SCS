package application;

import java.io.IOException;

import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainController implements ControlledScreen {

	ScreensController myController;
	@FXML
	private Button DoctorLoginBtn,HelperLoginBtn,PatientLoginBtn;
	
	public void goToDoctorLogin(ActionEvent event) throws IOException  {
//	Stage primaryStage = new Stage();
//	Parent root = FXMLLoader.load(getClass().getResource("Doctor.fxml"));
//	Scene scene = new Scene(root);
//	primaryStage.setScene(scene);
//	primaryStage.show();
	myController.setScreen(Main.screen4ID);
}
public void goToPatientLogin(ActionEvent event) throws IOException  {
	myController.setScreen(Main.screen2ID);
}
public void goToHelperLogin(ActionEvent event) throws IOException  {
	myController.setScreen(Main.screen3ID);
}
@Override
public void setScreenParent(ScreensController screenPage) {
	myController =screenPage;
}
}
