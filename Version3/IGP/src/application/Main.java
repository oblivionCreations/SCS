package application;
import java.util.ArrayList;
import java.util.List;
import application.Model.PatientProfile;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Doctor.fxml"));
			Scene scene = new Scene(root);
			//scene.getStylesheets() 	
			primaryStage.setTitle("Home");
			primaryStage.setScene(scene);
			primaryStage.show();			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
