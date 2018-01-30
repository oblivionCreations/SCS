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
				counter=1, 
				PatientID = 1, DBInsertInterval = 10;
	
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
					Gluc_Pump_Val=0,
					Refference_Glucagon,
					Total_Gluc_Pump_Val=0, 
					referanceGlucagonprogressbar=1,
					xRef,
					Total_max_Dosage,
					physicalActivities,
					physicalTemp,
					glucagonDose,
					gDoseTemp;

	
	public String eCarbs;
	
	@FXML
	public Button 	sensorONBtn,
					sensorOFFBtn,
					breakfastBtn,
					lunchBtn,
					dinnerBtn,
					goDownBtn,
					RI_1;
	
	@FXML
	private ProgressBar pb = new ProgressBar(); 
	private ProgressBar pb_glucagon = new ProgressBar();
	
	@FXML
	private Label reqIns;
	
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
	}

	Connection connection;
	// this turns on the system
	public void sensorON() {
		start = 1;
		System.out.println("SENSOR TURNED ON");
		pb.setProgress(1.0);
		pb_glucagon.setProgress(1.0);

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
					physicalActivities = Double.parseDouble(physicalText.getText());
					Refference_Glucagon= physicalActivities*3;
					physicalTemp = physicalActivities*5/40;	
					glucagonDose=((Current_BGL-110)+physicalActivities*2);
					gDoseTemp = glucagonDose/20;
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
						
					
					double itemp=insTemp, iValue = InsulinValue, min=0,curBgl=Current_BGL,refBgl=110,mtemp=mealTemp,mValue=mealValue;
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
					
					double itemp=insTemp, iValue = InsulinValue, min=0,curBgl=Current_BGL,refBgl=110,mtemp=mealTemp,mValue=mealValue;
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
					
					double itemp=insTemp, iValue = InsulinValue, min=0,curBgl=Current_BGL,refBgl=110,mtemp=mealTemp,mValue=mealValue;
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
							System.out.println(f.format(Current_BGL));
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
						System.out.println(DBCaller.InsertPatientHistory(patientHistory));
					}
						DBCounter++;
					// this sets the pump to the exact insulin remaining
					if(InsulinValue >0 ) {
						// to increase the capacity of the tank divide by a factor
						Ins_Pump_Val=(((Refference_Insuin-InsulinValue)/50)/ins_disp)/2;
						if(InsulinValue==0) {
							Total_Ins_Pump_Val=Total_Ins_Pump_Val+Ins_Pump_Val;
						}
						pb.setProgress(referanceInsulinprogressbar-(Ins_Pump_Val-Total_Ins_Pump_Val)/100);
						referanceInsulinprogressbar = referanceInsulinprogressbar-(Ins_Pump_Val-Total_Ins_Pump_Val)/100;
						System.out.println("Total_Ins_Pump_Val"+(referanceInsulinprogressbar-(Ins_Pump_Val-Total_Ins_Pump_Val)/100));
						System.out.println("Ins_Pump_Val"+Ins_Pump_Val);
					}	

					// this sets the pump to the exact glucagon remaining
					if(glucagonDose >0 ) {
						// to increase the capacity of the tank divide by a factor
						Gluc_Pump_Val=(((Refference_Glucagon-glucagonDose)/50)/gluc_disp)/2;
						if(glucagonDose==0) {
							Total_Gluc_Pump_Val=Total_Gluc_Pump_Val+Gluc_Pump_Val;
						}
						pb_glucagon.setProgress((referanceGlucagonprogressbar-(Gluc_Pump_Val-Total_Gluc_Pump_Val)/100));
						referanceGlucagonprogressbar = referanceGlucagonprogressbar-(Gluc_Pump_Val-Total_Gluc_Pump_Val)/100;
						System.out.println("Total_Gluc_Pump_Val"+(referanceGlucagonprogressbar-(Gluc_Pump_Val-Total_Gluc_Pump_Val)/100));
						System.out.println("Gluc_Pump_Val"+Gluc_Pump_Val);

					}
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

}




