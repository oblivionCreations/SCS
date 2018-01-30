package application;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;

public class Main extends Application {

	public static String screen1ID = "main";
    public static String screen1File = "HomeScreen.fxml";
    public static String screen2ID = "Model";
    public static String screen2File = "/application/Model/PatientLogin.fxml";
    public static String screen3ID = "Helper";
    public static String screen3File = "/application/Helper/HelperLogin.fxml";
    public static String screen4ID = "Doctor";
    public static String screen4File = "/application/DoctorLogin.fxml";
    public static String screen5ID = "DoctorInputScreen";
    public static String screen5File = "Doctor.fxml";
    public static String screen6ID = "PatientManualMode";
    public static String screen6File = "/application/Model/PatientManualScreen.fxml";
//	@Override
    public void start(Stage primaryStage) {
	
    
    ScreensController mainContainer = new ScreensController();
    mainContainer.loadScreen(Main.screen1ID, Main.screen1File);
    mainContainer.loadScreen(Main.screen2ID, Main.screen2File);
    mainContainer.loadScreen(Main.screen3ID, Main.screen3File);
    mainContainer.loadScreen(Main.screen4ID, Main.screen4File);
    mainContainer.loadScreen(Main.screen5ID, Main.screen5File);
    mainContainer.loadScreen(Main.screen6ID, Main.screen6File);
    
    mainContainer.setScreen(Main.screen1ID);
    
    Group root = new Group();
    root.getChildren().addAll(mainContainer);
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
