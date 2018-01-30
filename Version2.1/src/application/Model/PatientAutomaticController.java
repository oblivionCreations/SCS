package application.Model;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import application.ControlledScreen;
import application.ScreensController;
import application.Helper.DBCaller;




public class PatientAutomaticController extends Thread implements Initializable, ControlledScreen  {
	//implements Initializable for progress bar
	public int  start =0,
				startValue=10,
				weight, 
				ins_disp=10,
				gluc_disp = 10, 
				PatientID = 1, DBInsertInterval = 10, i=1, notificationcounter = 0;
	
	public double   Current_BGL=112,
					InsulinValue=0, 
					mealValue,
					BreakfastValue=0,
					lunchValue=0,
					dinnerValue=0,
					mealTemp,
					insTemp=0,
					req_Ins=0,
					CHO_ins_dose,
					Ins_Pump_Val=0,
					Refference_Insuin,
					Total_Ins_Pump_Val=0, 
					referanceInsulinprogressbar=1,
					refferenceBatery=1,
					Gluc_Pump_Val=0,
					Refference_Glucagon,
					Total_Gluc_Pump_Val=0, 
					referanceGlucagonprogressbar=1,
					xRef,
					counter=1.0,
					Total_max_Dosage,
					physicalActivities,
					physicalTemp,
					glucagonDose,
					gDoseTemp;

	
	public String eCarbs, SetText = "";
	
	@FXML
	public Button 	sensorONBtn,
					sensorOFFBtn,
					breakfastBtn,
					lunchBtn,
					dinnerBtn,
					goDownBtn,
					RI_1,
					RG_1,
					RB_1;
	
	@FXML
	private ProgressBar pb = new ProgressBar(); 
	@FXML
	private ProgressBar pb_glucagon = new ProgressBar();
	@FXML
	private ProgressBar pb_battery = new ProgressBar();
	
	@FXML
	private Label reqIns, Notification;
	
	@FXML
	private TextField   physicalText,
						bglVal,
						projectedTime;
	
	public boolean  mealBool = false,
					insBool=false,
					mealTime = false,
					physicalBool = false,
					alarm_ON = false,
					moreBGL= false,
					glucagonBool=false;
	@FXML
	private LineChart<String, Number> lineChart;
	XYChart.Series<String, Number> series = new XYChart.Series<>();
	XYChart.Series<String, Number> Insulinseries = new XYChart.Series<>();
	XYChart.Series<String, Number> Glucagonseries = new XYChart.Series<>();



	@FXML
	private MediaView mv;
	MediaPlayer mp;
	Media me;

	ScreensController myController;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Notification.setWrapText(true);
	}

	Connection connection;
	// this turns on the system
	public void sensorON() {
		start = 1;
		//System.out.println("SENSOR TURNED ON");
		pb.setProgress(1.0);
		//pb_glucagon.setProgress(1.0);

		String path = new File("src/Media/Emergency.mp3").getAbsolutePath();
		me = new Media(new File(path).toURI().toString());
		mp = new MediaPlayer(me);
		mv.setMediaPlayer(mp);

		startFunc();


	}

	public void startFunc() {
		try {
			startTask();
			// this pauses the system
			sensorOFFBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					start =0; 
					System.out.println("SENSOR TURNED OFF");
				}
			});
			// this will reduce the glucose levels by the physical activities
			goDownBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{   
					physicalBool = true;
					physicalActivities = Double.parseDouble(physicalText.getText())*2;
					physicalTemp = physicalActivities*5/40;	
					
					glucagonDose=((Current_BGL-110)+physicalActivities);
					gDoseTemp = glucagonDose/20;
					
					Refference_Glucagon= glucagonDose;
				}
			});
			// breakfast will take input values from the text box and evaluate and start acting on the body
			breakfastBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					
				//	CHO_ins_dose=(Current_BGL-110)/50+CHO_ins_dose;
				//	req_Ins = CHO_ins_dose;
						insBool = true;	
						mealTime = true;
						mealBool = true;
						BreakfastValue = 60;     // 60 gms 
						mealValue = BreakfastValue*3; // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
						InsulinValue = mealValue; // 60*5 mg/dL glucose value terms
						Refference_Insuin= InsulinValue;
						
					
					double itemp=insTemp, iValue = InsulinValue, min=0,curBgl=Current_BGL,mtemp=mealTemp,mValue=mealValue;
					while(curBgl>110) {
						itemp = iValue/25;
						iValue = iValue - itemp;
						if(iValue<0.5) {
							break;
						}
						mtemp = mValue/20;
						mValue = iValue - mtemp;
						curBgl=curBgl*0.9995+mtemp-itemp;
				 		min++;
						}	
				 		DecimalFormat f = new DecimalFormat("##.000");
				 		projectedTime.setText((f.format(min/60)));
				}
			});
			// lunch will take input values from the text box and evaluate and start acting on the body
			lunchBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{    
						insBool = true;	
						mealTime = true;
						mealBool = true;
						lunchValue = 45;     // 60 gms 
						mealValue = lunchValue*3; // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
						InsulinValue = mealValue; // 60*5 mg/dL glucose value terms
						Refference_Insuin= InsulinValue;
					
					double itemp=insTemp, iValue = InsulinValue, min=0,curBgl=Current_BGL,mtemp=mealTemp,mValue=mealValue;
					while(curBgl>110) {
						itemp = iValue/25;
						iValue = iValue - itemp;
						if(iValue<0.5) {
							break;
						}
						mtemp = mValue/20;
						mValue = iValue - mtemp;
						curBgl=curBgl*0.9995+mtemp-itemp;
				 		min++;
						}	
				 		DecimalFormat f = new DecimalFormat("##.000");
				 		projectedTime.setText((f.format(min/60)));
				}
			});
			// dinner will take input values from the text box and evaluate and start acting on the body
			dinnerBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{     
						insBool = true;	
						mealTime = true;
						mealBool = true;
						dinnerValue = 50;     // 60 gms 
						mealValue = dinnerValue*3; // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
						InsulinValue = mealValue; // 60*5 mg/dL glucose value terms
						Refference_Insuin= InsulinValue;
					
					double itemp=insTemp, iValue = InsulinValue, min=0,curBgl=Current_BGL,mtemp=mealTemp,mValue=mealValue;
					while(curBgl>110) {
						itemp = iValue/25;
						iValue = iValue - itemp;
						if(iValue<0.5) {
							break;
						}
						mtemp = mValue/20;
						mValue = iValue - mtemp;
						curBgl=curBgl*0.9995+mtemp-itemp;
				 		min++;
						}	
				 		DecimalFormat f = new DecimalFormat("##.000");
				 		projectedTime.setText((f.format(min/60)));
				}
			});
			// This will refill the Insulin tank
			RI_1.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					referanceInsulinprogressbar = 1.0;
					pb.setProgress(referanceInsulinprogressbar);
				}
			});
			RG_1.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					referanceGlucagonprogressbar=1.0;
					pb_glucagon.setProgress(referanceGlucagonprogressbar);
				}
			});
			RB_1.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					refferenceBatery = 1.0;
					pb_battery.setProgress(refferenceBatery);
				}
			});
			
		}
		catch(Exception e) {
			System.out.println();	
		}
	}

	public void startTask() {
		// these set the chart properties
		series = new XYChart.Series<>();
		series.setName("Variation of levels with Time");
		Insulinseries.setName("Insulin Threshold");
		Glucagonseries.setName("Glucagon Threshold");
		lineChart.getData().add(series);
		lineChart.getData().add(Insulinseries);
		lineChart.getData().add(Glucagonseries);
		lineChart.setAnimated(true);
		lineChart.getXAxis().setTickLabelsVisible(true);
		//lineChart.getYAxis().setAutoRanging(true);
		lineChart.setCreateSymbols(false);

		Runnable task = new Runnable()
		{
			public void run()
			{
				runTask();
			}
		};


		// Run the task in a background thread
		Thread backgroundThread = new Thread(task);
		// Terminate the running thread if the application exits
		backgroundThread.setDaemon(true);
		// Start the thread
		backgroundThread.start();
		//		prepareTimeline();

	}

	public void runTask() {
		try {
			while(start==1) {
				if(start==0) {
					break;
				}      //This is the code that executes when the sensor Turns ON.
				else {
					
					if(notificationcounter % 5 == 0)
					{
						//System.out.println(notificationcounter);
					SetNotification();notificationcounter++;
					}
					else
						notificationcounter++;
					// if BF or Lunch or Dinner is consumed --> say breakfast
					if(mealTime==true) {
						//if both meal and insulin are given inbetween or clashes by any chance
						if(mealBool==true && insBool==true) {
							if(Current_BGL>160){
								moreBGL=true;
							}
							// if still meal is remaining to disolve
							if(mealValue>0){mealTemp = mealValue/20; mealValue = mealValue -mealTemp;}else {mealTemp = 0;}
							// if still insulin is remaining to disolve
							if(moreBGL==true){
							if(InsulinValue>0)   {insTemp=InsulinValue/25; InsulinValue = InsulinValue - insTemp;}  else {insTemp=0;}
							}
							Current_BGL=Current_BGL*0.9999+mealTemp-insTemp;  
							if(mealValue<=0 && InsulinValue <= 0) {
								mealTime = false;
								moreBGL = false;
							}
						}
					}// if physical activities happen the glucose level starts droping
					else if(physicalBool==true) {
							if(physicalActivities>0){
							physicalTemp = physicalActivities*5/40;
							physicalActivities = physicalActivities - physicalTemp; 
							}else {physicalTemp=0;}
							if(glucagonDose>0){
							gDoseTemp = glucagonDose/20;
							glucagonDose = glucagonDose - gDoseTemp;
							}else{ gDoseTemp =0;}
							Current_BGL=Current_BGL*0.9999+gDoseTemp-physicalTemp;	
							if(physicalTemp<=0 && gDoseTemp<=0){
							physicalBool =	false;
							}
//							System.out.println("Current_BGL "+ Current_BGL);
//							System.out.println("gDoseTemp "+ gDoseTemp);
//							System.out.println("physicalTemp "+ physicalTemp);
					}
					// if nothing is given given or taken, and when the body is functioning normally 
					else {
						Current_BGL= Current_BGL*0.999756;
						// if bgl level exceeds 110, then it reduces by 0.14% of the current one.
						if(Current_BGL>110) {
							Current_BGL= Current_BGL*0.99855;
						}// if bgl level falls bellow 90, then it increases by 6.9% of the current one.
						else if(Current_BGL<90) {
							Current_BGL= Current_BGL*1.06923;
						}
						else if(Current_BGL<110 && Current_BGL>90) {
							Current_BGL= Current_BGL*0.99855;
						}
					}

					DecimalFormat f = new DecimalFormat("##.000");
					Platform.runLater(new Runnable() 
					{
						@Override 
						public void run() 
						{
							//System.out.println(f.format(Current_BGL));
						}
					});

					// This makes the graph of the obtained glucose values
					Data<String,Number> CurrentData = new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), Current_BGL);
					series.getData().add(CurrentData);	
					Insulinseries.getData().add(new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), 180));	
					Glucagonseries.getData().add(new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), 36));	

					// This maintains exactly 25 points on the X-axis
					if(series.getData().size() >25)
					{
						series.getData().remove(0, 1);
					Insulinseries.getData().remove(0, 1);
					Glucagonseries.getData().remove(0, 1);
					}

					// This turns on the alarm when the glucose Level falls in the danger zone of Current_BGL <= 36 or Current_BGL >= 180 mg/dL
					if(Current_BGL>=180 || Current_BGL <= 36) {
						mp.play();
						alarm_ON= true;

					}
					// This turns off the alarm when the glucose Level is not in the danger zone anymore,  36 < Current_BGL < 180 mg/dL
					if(alarm_ON== true && Current_BGL<180 && Current_BGL >36) {
						mp.stop();
					}
					int DBCounter = 0;
					if(DBCounter % DBInsertInterval==0) {
						PatientHistory patientHistory = new PatientHistory(Current_BGL,PatientID);
						DBCaller.InsertPatientHistory(patientHistory);
					}
						DBCounter++;
					// this sets the pump to the exact insulin remaining
						if(insBool==true && InsulinValue >0 ) {
							// to increase the capacity of the tank divide by a factor
							Ins_Pump_Val=(((Refference_Insuin-InsulinValue)/50)/ins_disp)/2;
							if(InsulinValue==0) {
								Total_Ins_Pump_Val=Total_Ins_Pump_Val+Ins_Pump_Val;
							}
							pb.setProgress(referanceInsulinprogressbar-(Ins_Pump_Val-Total_Ins_Pump_Val)/100);
							referanceInsulinprogressbar = referanceInsulinprogressbar-(Ins_Pump_Val-Total_Ins_Pump_Val)/100;
						}
					// this sets the pump to the exact glucagon remaining
					if(physicalBool==true && glucagonDose >0 ) {
						// to increase the capacity of the tank divide by a factor
						Gluc_Pump_Val=(((Refference_Glucagon-glucagonDose)/50)/gluc_disp)/2;
						
						if(glucagonDose==0) {
							Total_Gluc_Pump_Val=Total_Gluc_Pump_Val+Gluc_Pump_Val;
						}
					  //  pb_glucagon.setProgress((referanceGlucagonprogressbar-(Gluc_Pump_Val-Total_Gluc_Pump_Val)/100));
						referanceGlucagonprogressbar = referanceGlucagonprogressbar-(Gluc_Pump_Val-Total_Gluc_Pump_Val)/100;
						 pb_glucagon.setProgress(referanceGlucagonprogressbar);
							
					}
					refferenceBatery = 1 - (1-referanceInsulinprogressbar)/20-(1-referanceGlucagonprogressbar)/20 -counter/1200;
					pb_battery.setProgress(refferenceBatery);
					System.out.println("-------------------------------------------");
					System.out.println("referanceInsulinprogressbar = "+ (1-referanceInsulinprogressbar)/10);
					System.out.println("referanceGlucagonprogressbar = "+ (1-referanceGlucagonprogressbar)/10);
					System.out.println("counter = "+ counter/600);
					System.out.println("refferenceBatery = "+ refferenceBatery);
					System.out.println("-------------------------------------------");
					
				//	projectedTime.setText((f.format(referanceGlucagonprogressbar/2)));
					bglVal.setText((f.format(Current_BGL)));//f.format(x)

					Thread.sleep(500);
				}
				counter++;
			}
		}
		catch(Exception e) {

		}
	}

	@Override
	public void setScreenParent(ScreensController screenPage) {
		// TODO Auto-generated method stub
		myController =screenPage;
	}
public void SetNotification() {
	
	
	switch(i)
	{
	case(1):
		SetText = "1.	Lose extra weight. Moving toward a healthy weight helps control blood sugars. Your doctor, a dietitian, and a fitness trainer can get you started on a plan that will work for you.";
	i++;
	break;
	case(2):
		SetText = "2.	Check your blood sugar level at least twice a day. Is it in the range advised by your doctor? Also, write it down so you can track your progress and note how food and activity affect your levels.";
	i++;
	break;
	case(3):
		SetText = "3.	Get A1c blood tests to find out your average blood sugar for the past 2 to 3 months. Most people with type 2 diabetes should aim for an A1c of 7% or lower. Ask your doctor how often you need to get an A1c test.";
	i++;
	break;
	case(4):
		SetText = "4.	Track your carbohydrates. Know how many carbs you’re eating and how often you have them. Managing your carbs can help keep your blood sugar under control. Choose high-fibre carbs, such as green vegetables, fruit, beans, and whole grains.";
	i++;
	break;
	case(5):
		SetText = "5.	Control your blood pressure, cholesterol, and triglyceride levels. Diabetes makes heart disease more likely, so keep a close eye on your blood pressure and cholesterol. Talk with your doctor about keeping your cholesterol, triglycerides, and blood pressure in check. Take medications as prescribed.";
	i++;
	break;
	case(6):
		SetText = "6.	Keep moving. Regular exercise can help you reach or maintain a healthy weight. Exercise also cuts stress and helps control blood pressure, cholesterol, and triglyceride levels. Get at least 30 minutes a day of aerobic exercise 5 days a week. Try walking, dancing, low-impact aerobics, swimming, tennis, or a stationary bike. Start out more slowly if you aren't active now. You can break up the 30 minutes -- say, by taking a 10-minute walk after every meal. Include strength training and stretching on some days, too.";
	i++;
	break;
	case(7):
		SetText = "7.	Catch some ZZZs. When you’re sleep-deprived, you tend to eat more, and you can put on weight, which leads to health problems. People with diabetes who get enough sleep often have healthier eating habits and improved blood sugar levels.";
	i++;
	break;
	case(8):
		SetText = "8.	Manage stress. Stress and diabetes don't mix. Excess stress can elevate blood sugar levels. But you can find relief by sitting quietly for 15 minutes, meditating, or practicing yoga.";
	i++;
	break;
	case(9):
		SetText = "9.	See your doctor. Get a complete check-up at least once a year, though you may talk to your doctor more often. At your annual physical, make sure you get a dilated eye exam, blood pressure check, foot exam, and screenings for other complications such as kidney damage, nerve damage, and heart disease.";
	i=1;
	break;
		
	}
	//System.out.println(SetText);
	 Platform.runLater(new Runnable() {
         @Override public void run() {
        	 Notification.setText(SetText);
         }
     });
	
	
}
}

	


