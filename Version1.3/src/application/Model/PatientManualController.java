package application.Model;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
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




public class PatientManualController extends Thread implements Initializable, ControlledScreen  {
	//implements Initializable for progress bar
	public int  start =0,
				startValue=10,
				weight, 
				ins_disp=10,
				gluc_disp = 10,
				counter=1, 
				PatientID = 1;
	
	public double   Current_BGL=112,
					InsulinValue=0, 
					NextInsulinValue =0,
					mealValue,
					BreakfastValue=0,
					lunchValue=0,
					dinnerValue=0,
					bfTemp=0,
					lnTemp,
					dinTemp,
					ecTemp,
					mealTemp,
					extraCarbohydrates,
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
					preInsulin,
					preInsulinTemp,
					physicalActivities, 
					physicalTemp,
					glucagonDose,
					gDoseTemp;

	
	//initial concentration of carbohydrates
	double A0 = 121.7;
	// rate of food digestion(Glycemic Index)
	double k1= 0.0453;
	// rate at which insulin is released
	double k2 = 0.0224;
	public static final double e = 2.71828;
	
	public String eCarbs;
	
	@FXML
	public Button 	sensorONBtn,
					breakfastBtn,
					lunchBtn,
					dinnerBtn,
					RI_1,
					startInsulin_1,
					sensorOFFBtn,
					checkBFInsBtn,
					inputCarbs,
					checkLunchInsBtn,
					checkDinnerInsBtn,
					goDownBtn,
					startGlucagon;
	
	@FXML
	private ProgressBar pb = new ProgressBar(); 
	private ProgressBar pb_glucagon = new ProgressBar();
	
	@FXML
	private Label reqIns;
	
	@FXML
	private TextField   InputVal, 
						BFText, 
						LunchText, 
						DinnerText, 
						physicalText,
						bglVal,
						projectedTime;
	
	public boolean  mealBool = false,
					insBool=false,
					bfBool=false,
					lnBool=false,
					dinBool=false,
					extraInsBool = false,

					mealTime = false,
					breakfastTime = false, 
					lunchTime = false , 
					dinnerTime = false, 
					extraCarbsTime = false,

					checkBFInsBool = false,
					checkLunchInsBool = false,
					checkDinnerInsBool = false,
					physicalBool = false,
					glucagonBool = false,
					alarm_ON = false;
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
			// this will add extra carbohydrates inbetween the meals
			inputCarbs.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{   
					if(insBool==false) {
						extraCarbsTime = true;
						bfBool=true;
						mealTime = true;
						mealBool = true;
						extraCarbohydrates = mealValue/3+ Double.parseDouble(InputVal.getText());     // 60 gms 
						mealValue = extraCarbohydrates*3; // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
						InsulinValue = mealValue; // 60*5 mg/dL glucose value terms
						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
						System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);
					}
					else if(insBool==true) {
						extraCarbsTime = true;
						bfBool=true;
						mealTime = true;
						mealBool = true;
						extraCarbohydrates = mealValue/3+ Double.parseDouble(InputVal.getText());     // 60 gms 
						mealValue = extraCarbohydrates*3; // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
						NextInsulinValue = mealValue;
						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
						System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);
					}
					System.out.println("breakfastTime is " + breakfastTime);
					}
			});
			// this will reduce the glucose levels by the physical activities
			goDownBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{   
					System.out.println("coming inside PHYSICAL ACTIVITY");
					physicalBool = true;
					physicalActivities = Double.parseDouble(physicalText.getText());
					Refference_Glucagon= physicalActivities*3;
					physicalTemp = physicalActivities*5/40;
					

				}
			});
			// breakfast will take input values from the text box and evaluate and start acting on the body
			breakfastBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      

					
					if(insBool==false) {
						breakfastTime = true;
						bfBool=true;
						mealTime = true;
						mealBool = true;
						BreakfastValue = Double.parseDouble(BFText.getText());     // 60 gms 
						mealValue = BreakfastValue*3; // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
					if(checkBFInsBool==false) {
						InsulinValue = mealValue; // 60*5 mg/dL glucose value terms
						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
						System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);
					}
					else if(checkBFInsBool==true) {
						InsulinValue=preInsulin;
						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
						preInsulin=0;
						checkBFInsBool=false;
					}
					}
					else if(insBool==true) {
						breakfastTime = true;
						bfBool=true;
						mealTime = true;
						mealBool = true;
						BreakfastValue = mealValue/3 + Double.parseDouble(BFText.getText());     // 60 gms 
						mealValue = BreakfastValue*3; // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
						System.out.println("BreakfastValue --> "+BreakfastValue);
						if(checkBFInsBool==false) {
							if(InsulinValue==0) {
								InsulinValue=mealValue;
							}else{
								NextInsulinValue = mealValue;
							}
							Refference_Insuin= InsulinValue;
							insTemp = InsulinValue/25;
							System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);
						}
						else if(checkBFInsBool==true) {
							InsulinValue=preInsulin + BreakfastValue*3;;
							Refference_Insuin= InsulinValue;
							insTemp = InsulinValue/25;
							preInsulin=0;
							checkBFInsBool=false;
						}
					}
					System.out.println("breakfastTime is " + breakfastTime);
				}
			});
			// lunch will take input values from the text box and evaluate and start acting on the body
			lunchBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					if(insBool == false) {
					lunchTime = true;
					lnBool=true;
					lunchValue = Double.parseDouble(LunchText.getText());      // 45 gms 	
					lnTemp = lunchValue/40*5; // rate of disolution of carbs into the blood
					mealTime = true;
					mealBool = true;
					mealValue = lunchValue*3;    // 60 * 5 mg/dL glucose value terms
					mealTemp = mealValue/20;
					if(checkLunchInsBool==false) {
						InsulinValue = lunchValue*3; // 60*5 mg/dL glucose value terms
						System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);

						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
					}
					else if(checkLunchInsBool==true) {
						InsulinValue=preInsulin;
						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
						preInsulin=0;
						checkBFInsBool=false;
					}
					}
					else if(insBool == true) {
						lunchTime = true;
						lnBool=true;
						lunchValue = mealValue/3 + Double.parseDouble(LunchText.getText());      // 45 gms 	
						lnTemp = lunchValue/40*5; // rate of disolution of carbs into the blood
						mealTime = true;
						mealBool = true;
						mealValue = lunchValue*3;    // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
						if(checkLunchInsBool==false) {
							System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);
							if(InsulinValue==0) {
								InsulinValue=mealValue;
							}else{
								NextInsulinValue = mealValue;
							}
							Refference_Insuin= InsulinValue;
							insTemp = InsulinValue/25;
						}
						else if(checkLunchInsBool==true) {
							InsulinValue=preInsulin;
							Refference_Insuin= InsulinValue;
							insTemp = InsulinValue/25;
							preInsulin=0;
							checkBFInsBool=false;
						}
						}
					System.out.println("lunchTime is " + lunchTime);
				}
			});
			// dinner will take input values from the text box and evaluate and start acting on the body
			dinnerBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					if(insBool==false) {
					dinnerTime = true;
					dinBool=true;
					dinnerValue = Double.parseDouble(DinnerText.getText());     // 30 gms 	
					dinTemp = dinnerValue/40*5; // rate of disolution of carbs into the blood
					mealTime = true;
					mealBool = true;
					mealValue = dinnerValue*3;  // 60 * 5 mg/dL glucose value terms
					mealTemp = mealValue/20;
					if(checkDinnerInsBool==false) {
						InsulinValue = dinnerValue*3; // 60*5 mg/dL glucose value terms
						System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);

						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
					}
					else if(checkDinnerInsBool==true) {
						InsulinValue=preInsulin;
						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
						preInsulin=0;
						checkBFInsBool=false;
					}
					System.out.println("dinnerTime is " + dinnerTime);
					//					InsulinValue = mealValue; // 60*5 mg/dL glucose value terms
					//					insTemp = InsulinValue/25;
				     }
					else if(insBool==true) {
						dinnerTime = true;
						dinBool=true;
						dinnerValue = mealValue/3 + Double.parseDouble(DinnerText.getText());     // 30 gms 	
						dinTemp = dinnerValue/40*5; // rate of disolution of carbs into the blood
						mealTime = true;
						mealBool = true;
						mealValue = dinnerValue*3;  // 60 * 5 mg/dL glucose value terms
						mealTemp = mealValue/20;
						if(checkDinnerInsBool==false) {
							System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);
							if(InsulinValue==0) {
								InsulinValue=mealValue;
							}else{
								NextInsulinValue = mealValue;
							}
							Refference_Insuin= InsulinValue;
							insTemp = InsulinValue/25;
						}
						else if(checkDinnerInsBool==true) {
							InsulinValue=preInsulin;
							Refference_Insuin= InsulinValue;
							insTemp = InsulinValue/25;
							preInsulin=0;
							checkBFInsBool=false;
						}
						System.out.println("dinnerTime is " + dinnerTime);
						//					InsulinValue = mealValue; // 60*5 mg/dL glucose value terms
						//					insTemp = InsulinValue/25;
					     }
					}
			});
			// this will take input from the text box and then calculate required insulin
			checkBFInsBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					checkBFInsBool = true;

					BreakfastValue = Double.parseDouble(BFText.getText());
					Total_max_Dosage = weight * 0.55;
					System.out.println("coming to line 1");
					CHO_ins_dose=BreakfastValue/ins_disp;
					CHO_ins_dose=(Current_BGL-110)/50+CHO_ins_dose;
					req_Ins = CHO_ins_dose;
					preInsulin=BreakfastValue*3+(Current_BGL-110);
					preInsulinTemp=preInsulin/25;

					System.out.println("coming to line 2");
					DecimalFormat f = new DecimalFormat("##.000");
					if(mealTime == true) {
						reqIns.setText("Its MealTime");
					}
					else {

						System.out.println("coming to line 3");
						reqIns.setText(f.format(req_Ins));
					}
					BreakfastValue = 0; 
					CHO_ins_dose = 0;
					req_Ins = 0;

				}
			});
			// this will take input from the text box and then calculate required insulin
			checkLunchInsBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					checkLunchInsBool = true;
					lunchValue = Double.parseDouble(LunchText.getText());  
					Total_max_Dosage = weight * 0.55;
					CHO_ins_dose=lunchValue/ins_disp;
					CHO_ins_dose=(Current_BGL-110)/50+CHO_ins_dose;
					req_Ins = CHO_ins_dose;
					preInsulin=lunchValue*3+(Current_BGL-110);
					preInsulinTemp=preInsulin/25;
					DecimalFormat f = new DecimalFormat("##.000");
					if(mealTime == true) {
						reqIns.setText("Its MealTime");
					}
					else {
						reqIns.setText(f.format(req_Ins));
					}
					lunchValue = 0; 
					CHO_ins_dose = 0;
					req_Ins = 0;
				}
			});
			// this will take input from the text box and then calculate required insulin
			checkDinnerInsBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{     
					checkDinnerInsBool = true;
					dinnerValue = Double.parseDouble(DinnerText.getText()); 
					Total_max_Dosage = weight * 0.55;
					CHO_ins_dose=dinnerValue/ins_disp;
					CHO_ins_dose=(Current_BGL-110)/50+CHO_ins_dose;
					req_Ins = CHO_ins_dose;
					preInsulin=dinnerValue*3+(Current_BGL-110);
					preInsulinTemp=preInsulin/25;
					DecimalFormat f = new DecimalFormat("##.000");
					if(mealTime == true) {
						reqIns.setText("Its MealTime");
					}
					else {
						reqIns.setText(f.format(req_Ins));
					}
					dinnerValue = 0; 
					CHO_ins_dose = 0;
					req_Ins = 0;
				}
			});
			// This will refill the Insulin tank
			RI_1.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      

				}
			});
			// this will start injecting insulin when pressed
			startInsulin_1.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					insBool = true;	
					//CHO_ins_dose=dinnerValue/ins_disp;
					CHO_ins_dose=(Current_BGL-110)/50+CHO_ins_dose;
					req_Ins = CHO_ins_dose;
					if(NextInsulinValue !=0) {
						InsulinValue = NextInsulinValue;
						Refference_Insuin= InsulinValue;
						NextInsulinValue=0;
					}
					
					double itemp=insTemp, iValue = InsulinValue, min=0,curBgl=Current_BGL,refBgl=110,mtemp=mealTemp,mValue=mealValue;
					while(curBgl>110) {
						itemp = iValue/25;
						iValue = iValue - itemp;
						mtemp = mValue/20;
						mValue = iValue - mtemp;
						curBgl=curBgl*0.9995+mtemp-itemp;
				 		min++;
				 		}
					System.out.println("curBgl is "+ curBgl+ "refBgl is "+ refBgl);
				 		System.out.println("Your body would be neutralized in "+ min+" iterations and it takes "+ min*10+" Muntes to finish.");
				 	
				 		 DecimalFormat f = new DecimalFormat("##.000");
				 		projectedTime.setText((f.format(min/240)));

				}
			});
			// this will start injecting glucagon
			startGlucagon.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					glucagonBool = true;	
					//CHO_ins_dose=dinnerValue/ins_disp;
					glucagonDose=((Current_BGL-110)+Double.parseDouble(physicalText.getText())*2);
					gDoseTemp = glucagonDose/20;
					System.out.println("glucagonDose is "+glucagonDose+" gDoseTemp is "+gDoseTemp);
					System.out.println("STARTED GLUCAGON");

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
					// This logic is when the patient takes extra carbohydrates inbetween his meal
					if(extraCarbsTime== true||extraInsBool == true) {
						System.out.println("extraCarbohydrates is " + mealValue + " and the InsulinValue is "+ InsulinValue+" and extraCarbsTime is "+extraCarbsTime);
							
						 // no insulin given but just intake of carbohydrate happened
						if(mealValue>0 && insBool == false) {
							mealTemp = mealValue/20;
							mealValue = mealValue - mealTemp;
							Current_BGL=Current_BGL*0.9995+mealTemp;
						}// insulin injected when still carbohydrates are yet to complete to disolve
						else if(insBool == true && extraCarbohydrates>0) {
							if(mealValue>0) {mealTemp = mealValue/20; mealValue = mealValue -mealTemp;}else {mealTemp = 0;}
							if(InsulinValue>0)   {insTemp=InsulinValue/25; InsulinValue = InsulinValue - insTemp;}  else {insTemp=0;}
							Current_BGL=Current_BGL*0.9995+mealTemp-insTemp;  
						}// insulin is still acting when the extra carbohydrates have completely mixed in the blood
						else if(insBool == true && mealValue<=0) {
							insTemp=InsulinValue/25;
							InsulinValue = InsulinValue - insTemp;
							Current_BGL=Current_BGL*0.9995-insTemp;	
						}// this leads to make it not enter into this condition in the next iteration
						else {
							extraCarbsTime = false;
						}
						if(mealValue<0) {
							extraCarbsTime = false;
						}
						if(InsulinValue<0) {
							insBool = false;
						}
						if(mealValue<=0 && InsulinValue<=0) {
							extraInsBool = false;
						}

					}// if BF or Lunch or Dinner is consumed --> say breakfast
					else if(mealTime==true || insBool == true) {
						
						System.out.println("mealValue is " + mealValue + " and the InsulinValue is "+ InsulinValue+" and checkBFInsBool is "+checkBFInsBool);
						
						// if the breakfast was taken and there is no insulin
						if(mealBool==true && insBool == false) {
							System.out.println("it is in condition 1");
							// if breakfast is still not mixed in the blood
							if((mealValue)>0) {
								mealTemp = mealValue/20;
								mealValue= mealValue - mealTemp;
								Current_BGL=Current_BGL*0.9995+mealTemp;
							}// if breakfast is completely mixed in the blood
							else if(mealValue<=0) {
								mealBool= false;
							}
						}
						// if there is no breakfast to disolve and only insulin acting 
						else if(mealBool == false && insBool==true) {
							System.out.println("it is in condition 2 and mealBool is " +mealBool + ", insBool is "+insBool);
							
							// if there is still insulin left to act in the blood after injection
							if(InsulinValue>0) 
							{
								insTemp=InsulinValue/25;
								InsulinValue = InsulinValue - insTemp;
								Current_BGL=Current_BGL*0.9995-insTemp; 
							}//this will induce insulin for required carbs before the meal
							else if(InsulinValue<=0) {
								// patient checks either of these 3 meals to check required Insulin to inject
								if(checkDinnerInsBool ||checkLunchInsBool ||checkBFInsBool ) {
									//before giving the meal if the insulin injected is still left to be disolved into the blood
									if(preInsulin>0) {
										preInsulinTemp=preInsulin/25;
										preInsulin = preInsulin - preInsulinTemp;
										Current_BGL=Current_BGL*0.9995-preInsulinTemp; 
									}// if everything is done the we should not enter again into this condition
								}else {
									insBool= false;
									checkDinnerInsBool= false;
									checkLunchInsBool=false;
									checkBFInsBool=false;
								}
							}  
						}// if both meal and insulin are given inbetween or clashes by any chance
						else if(mealBool==true && insBool==true) {
							System.out.println("it is in condition 3");
							// if still meal is remaining to disolve
							if(mealValue>0) {mealTemp = mealValue/20; mealValue = mealValue -mealTemp;}else {mealTemp = 0;}
							// if still insulin is remaining to disolve
							if(InsulinValue>0)   {insTemp=InsulinValue/25; InsulinValue = InsulinValue - insTemp;}  else {insTemp=0;}
							Current_BGL=Current_BGL*0.9999+mealTemp-insTemp;  


							if(mealValue<=0) {
								mealBool= false;

							}
							if(InsulinValue <= 0) {
								insBool = false;

							}
						}//when both are finished in next iteration this condition should not be satisfied
						if(mealBool==false && insBool==false) {
							mealTime = false;
						}// GLUCAGON activity must start here.
						 // if physical activities happen the glucose level starts droping
					}else if(physicalBool==true) {
						System.out.println("physicalActivities is "+ physicalActivities+" physicalTemp is "+ physicalTemp);
						// if activities are going on and level keeps going down continuesly.
						if(physicalActivities > 0) {
							physicalTemp = physicalActivities*5/40;
							physicalActivities = physicalActivities - physicalTemp; 
							Current_BGL=Current_BGL*0.9999+mealTemp-physicalTemp;
						}// if activities are finished then glucose level completely drops and remains stable
						else {
							physicalBool = false;
						}// if glucagon injection is not completely finished
						if(glucagonBool== true){
							gDoseTemp = glucagonDose/20;
							glucagonDose = glucagonDose - gDoseTemp;
							Current_BGL=Current_BGL*0.9999+gDoseTemp-physicalTemp;	
							System.out.println("glucagonDose is "+glucagonDose+" gDoseTemp is "+gDoseTemp);

						}
					}
					// if nothing is given given or taken, and when the body is functioning normally 
					else {
						System.out.println(" ............ Here in normal state");
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
					series.getData().add(new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), Current_BGL));	
				//	Insulinseries.getData().add(new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), 180));	
				//	Glucagonseries.getData().add(new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), 36));	

					// This maintains exactly 25 points on the X-axis
					if(series.getData().size() >25)
					{
						series.getData().remove(0, 1);
					//	Insulinseries.getData().remove(0, 1);
					//	Glucagonseries.getData().remove(0, 1);
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

					PatientHistory patientHistory = new PatientHistory(Current_BGL,PatientID);
					System.out.println(DBCaller.InsertPatientHistory(patientHistory));

					// this sets the pump to the exact insulin remaining
					if(insBool==true && InsulinValue >0 ) {
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
					if(glucagonBool==true && glucagonDose >0 ) {
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




