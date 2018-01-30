package application.Model;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
//import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import application.ControlledScreen;
import application.ScreensController;
import application.Helper.DBCaller;




public class PatientAutomaticController extends Thread implements Initializable, ControlledScreen  {
	//implements Initializable for progress bar
	public int start =0,startValue=10,weight, ins_disp=10,counter=1, PatientID = 1;
	public double   Current_BGL=112, InsulinValue=0, mealValue,
			BreakfastValue=0,lunchValue=0,dinnerValue=0,
			bfTemp=0,lnTemp,dinTemp,ecTemp,mealTemp,
			extraCarbohydrates,
			insTemp=0,
			req_Ins=0,
			CHO_ins_dose,
			Ins_Pump_Val=0,Refference_Insuin,Total_Ins_Pump_Val=0, referanceInsulinprogressbar=1,
			xRef,
			Total_max_Dosage,
			preInsulin,preInsulinTemp,
			physicalActivities, physicalTemp,glucagonDose,gDoseTemp;
	
	ScreensController myController;
	//initial concentration of carbohydrates
	double A0 = 121.7;
	// rate of food digestion(Glycemic Index)
	double k1= 0.0453;
	// rate at which insulin is released
	double k2 = 0.0224;
	public static final double e = 2.71828;
	public String eCarbs;
	@FXML
	public Button sensorONBtn,breakfastBtn,lunchBtn,dinnerBtn,RI_1,startInsulin_1,sensorOFFBtn,bglBtn,
	checkBFInsBtn,inputCarbs,checkLunchInsBtn,checkDinnerInsBtn,goDownBtn,startGlucagon;
	@FXML
	private ProgressBar pb = new ProgressBar();

	@FXML
	private Label bglVal,reqIns;
	@FXML
	private TextField InputVal, physicalText;
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
			glucagonBool = false;
	@FXML
	private LineChart<String, Number> lineChart;
	XYChart.Series<String, Number> series = new XYChart.Series<>();
	XYChart.Series<String, Number> Insulinseries = new XYChart.Series<>();
	XYChart.Series<String, Number> Glucagonseries = new XYChart.Series<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	Connection connection;

	public void sensorON() {
		start = 1;
		System.out.println("SENSOR TURNED ON");
		pb.setProgress(1.0);
		startFunc();
		
	}


	public void startFunc() {
		try {
			startTask();

			sensorOFFBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					start =0; 
					System.out.println("SENSOR TURNED OFF");
				}
			});

			bglBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{   
					DecimalFormat f = new DecimalFormat("##.000");
					//bglVal.setText(Double.toString(x));f.format(x)
					bglVal.setText(f.format(Current_BGL));
				}
			});

			inputCarbs.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{   
					extraCarbsTime = true;
					extraInsBool = true;
					extraCarbohydrates = Double.parseDouble(InputVal.getText());     // 60 gms 
					mealTime = true;
					mealBool = true;
					mealValue = extraCarbohydrates*3; // 60 * 5 mg/dL glucose value terms
					mealTemp = mealValue/20;
					extraCarbohydrates=extraCarbohydrates*3; // * 5 mg/dL glucose value terms
					ecTemp = extraCarbohydrates/25; // rate of disolution of carbs into the blood
					InsulinValue = extraCarbohydrates; // 60*5 mg/dL glucose value terms
					System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);

					Refference_Insuin= InsulinValue;
					insTemp = InsulinValue/25;
					
				System.out.println("inputCarbs Time is " + inputCarbs);
				
				}
			});
			goDownBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{   
					System.out.println("coming inside PHYSICAL ACTIVITY");
					physicalBool = true;
					physicalActivities = Double.parseDouble(physicalText.getText());
					physicalTemp = physicalActivities*5/40;

				}
			});


			breakfastBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      

					breakfastTime = true;
					bfBool=true;
					BreakfastValue = 60;     // 60 gms 
					bfTemp = BreakfastValue/40*5; // rate of disolution of carbs into the blood
					mealTime = true;
					mealBool = true;
					mealValue = BreakfastValue*3; // 60 * 5 mg/dL glucose value terms
					mealTemp = mealValue/20;

					if(checkBFInsBool==false) {
						InsulinValue = BreakfastValue*3; // 60*5 mg/dL glucose value terms
						System.out.println("comes here into check bool is "+checkBFInsBool+ " and insuline is "+ InsulinValue);

						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
					}
					else if(checkBFInsBool==true) {
						InsulinValue=preInsulin;
						Refference_Insuin= InsulinValue;
						insTemp = InsulinValue/25;
						preInsulin=0;
						checkBFInsBool=false;
					}
				System.out.println("breakfastTime is " + breakfastTime);
				}
			});

			lunchBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      

					lunchTime = true;
					lnBool=true;
					lunchValue = 45;      // 45 gms 	
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
					System.out.println("lunchTime is " + lunchTime);
				}
			});
			dinnerBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      

					dinnerTime = true;
					dinBool=true;
					dinnerValue = 30;     // 30 gms 	
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
			});

			checkBFInsBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					checkBFInsBool = true;
						
					BreakfastValue = 60;
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
			checkLunchInsBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					checkLunchInsBool = true;
					lunchValue = 45;  
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
			checkDinnerInsBtn.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{     
					checkDinnerInsBool = true;
					dinnerValue = 30; 
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

			RI_1.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      

				}
			});

			startInsulin_1.setOnAction(new EventHandler <ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{      
					insBool = true;	
					//CHO_ins_dose=dinnerValue/ins_disp;
					CHO_ins_dose=(Current_BGL-110)/50+CHO_ins_dose;
					req_Ins = CHO_ins_dose;

				}
			});
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

	public double power(double a, double b)
	{
		return Math.pow(a, b);
	}

	public void runTask() {
		try {
			while(start==1) {
				if(start==0) {
					break;
				}      //This is the code that executes when the sensor Turns ON.
				else {
					
					if(extraCarbsTime== true||extraInsBool == true) {
						System.out.println("extraCarbohydrates is " + extraCarbohydrates + " and the InsulinValue is "+ InsulinValue+" and extraCarbsTime is "+extraCarbsTime);
						
						if(extraCarbohydrates>0 && insBool == false) {
							ecTemp = extraCarbohydrates/20;
							extraCarbohydrates = extraCarbohydrates - ecTemp;
							Current_BGL=Current_BGL*0.9995+ecTemp;
						}
						else if(insBool == true && extraCarbohydrates>0) {
							if(extraCarbohydrates>0) {ecTemp = extraCarbohydrates/20; extraCarbohydrates = extraCarbohydrates -ecTemp;}else {ecTemp = 0;}
							if(InsulinValue>0)   {insTemp=InsulinValue/25; InsulinValue = InsulinValue - insTemp;}  else {insTemp=0;}
							Current_BGL=Current_BGL*0.9999+ecTemp-insTemp;  
							
						}else if(insBool == true && extraCarbohydrates<=0) {
							insTemp=InsulinValue/25;
							InsulinValue = InsulinValue - insTemp;
							Current_BGL=Current_BGL*0.9995-insTemp;	
						}
						else {
							extraCarbsTime = false;
						}
						if(extraCarbohydrates<0) {
							extraCarbsTime = false;
						}
						if(InsulinValue<0) {
							insBool = false;
						}
						if(extraCarbohydrates<=0 && InsulinValue<=0) {
							extraInsBool = false;
						}

					}
					else if(mealTime==true || insBool == true) {
						// if the breakfast was taken and there is no insulin
						System.out.println("mealValue is " + mealValue + " and the InsulinValue is "+ InsulinValue+" and checkBFInsBool is "+checkBFInsBool);
						if(mealBool==true && insBool == false) {
							System.out.println("it is in condition 1");
							if((mealValue)>0) {
								mealTemp = mealValue/20;
								mealValue= mealValue - mealTemp;
								Current_BGL=Current_BGL*0.9995+mealTemp;
							}
							else if(mealValue<=0) {
								mealBool= false;
							}
						}
						// if there is no breakfast to disolve and only insulin acting 
						else if(mealBool == false && insBool==true) {
							System.out.println("it is in condition 2 and mealBool is " +mealBool + ", insBool is "+insBool);

							if(InsulinValue>0) 
							{
								insTemp=InsulinValue/25;
								InsulinValue = InsulinValue - insTemp;
								Current_BGL=Current_BGL*0.9995-insTemp; 
							}//this will induce insulin for required carbs before the meal
							else if(InsulinValue<=0) {
								if(checkDinnerInsBool ||checkLunchInsBool ||checkBFInsBool ) {
									if(preInsulin>0) {
										preInsulinTemp=preInsulin/25;
										preInsulin = preInsulin - preInsulinTemp;
										Current_BGL=Current_BGL*0.9995-preInsulinTemp; 
									}
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
							if(mealValue>0) {mealTemp = mealValue/20; mealValue = mealValue -mealTemp;}else {mealTemp = 0;}
							if(InsulinValue>0)   {insTemp=InsulinValue/25; InsulinValue = InsulinValue - insTemp;}  else {insTemp=0;}
							Current_BGL=Current_BGL*0.9999+mealTemp-insTemp;  
							

							if(mealValue<=0) {
								mealBool= false;

							}
							if(InsulinValue <= 0) {
								insBool = false;

							}
						}
						if(mealBool==false && insBool==false) {
							mealTime = false;
						}
					}else if(physicalBool==true) {
						System.out.println("physicalActivities is "+ physicalActivities+" physicalTemp is "+ physicalTemp);
						if(physicalActivities > 0) {
							physicalTemp = physicalActivities*5/40;
							physicalActivities = physicalActivities - physicalTemp; 
							Current_BGL=Current_BGL*0.9999+mealTemp-physicalTemp;
						}
						else {
							physicalBool = false;
						}
						if(glucagonBool== true){
							gDoseTemp = glucagonDose/20;
							glucagonDose = glucagonDose - gDoseTemp;
							Current_BGL=Current_BGL*0.9999+gDoseTemp-physicalTemp;	
							System.out.println("glucagonDose is "+glucagonDose+" gDoseTemp is "+gDoseTemp);
							
						}
					}

					else {
						System.out.println(" ............ Here in normal state");
						Current_BGL= Current_BGL*0.999756;
						if(Current_BGL>110) {

							Current_BGL= Current_BGL*0.99855;
						}
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
					/*
					String sql = "INSERT INTO PatientProfile(name,Address,Age,AdditionalDrugs,ContactNo) VALUES(?,?,?,?,?)";
					 int status = 2;
				        try (Connection conn = SQLLiteConnection.Connecter();
				            PreparedStatement pstmt = conn.prepareStatement(sql)) {
				            pstmt.setString(1, patientProfile.getName());
				            pstmt.setString(2, patientProfile.getAddress());
				            pstmt.setInt(3, patientProfile.getAge());
				            pstmt.setString(4, patientProfile.getAdditionalDrugs());
				            pstmt.setString(5, patientProfile.getContactNo());
				          status =  pstmt.executeUpdate();
				        } catch (SQLException e) {
				            System.out.println(e.getMessage());
				        }
				        */

					// This makes the graph of the obtained glucose values
					//series.getData().add(new XYChart.Data<String, Number>(counter + " ", Current_BGL));	
					series.getData().add(new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), Current_BGL));	
					Insulinseries.getData().add(new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), 180));	
					Glucagonseries.getData().add(new XYChart.Data<String, Number>(LocalDateTime.now().toString().substring(11, 19), 90));	
					
					//bglVal.setText(f.format(Current_BGL));
					
					if(series.getData().size() >25)
					{
						series.getData().remove(0, 1);
						Insulinseries.getData().remove(0, 1);
						Glucagonseries.getData().remove(0, 1);
					}
					
					
					PatientHistory patientHistory = new PatientHistory(Current_BGL,PatientID);
					System.out.println(DBCaller.InsertPatientHistory(patientHistory));
					

					if(insBool==true && InsulinValue >0 ) {
						// to increase the capacity of the tank divide by a factor
						Ins_Pump_Val=(((Refference_Insuin-InsulinValue)/50)/ins_disp)/2;
						if(InsulinValue==0) {
							Total_Ins_Pump_Val=Total_Ins_Pump_Val+Ins_Pump_Val;
						}
						System.out.println("Total_Ins_Pump_Val"+Total_Ins_Pump_Val);
						System.out.println("Ins_Pump_Val"+Ins_Pump_Val);


						pb.setProgress(referanceInsulinprogressbar-(Ins_Pump_Val-Total_Ins_Pump_Val)/100);
						referanceInsulinprogressbar = referanceInsulinprogressbar-(Ins_Pump_Val-Total_Ins_Pump_Val)/100;
					}	

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




