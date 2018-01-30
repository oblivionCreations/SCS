package application.Model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class PatientHistory {
	
	public SimpleStringProperty  DateOfTheReading;
	public SimpleStringProperty timeOfTheReading;
	public SimpleDoubleProperty GlucoseValue;
	//public SimpleDoubleProperty dosage;
	//public SimpleStringProperty criticality;
	public SimpleStringProperty getDateOfTheReading() {
		return DateOfTheReading;
	}

	public void setDateOfTheReading(SimpleStringProperty dateOfTheReading) {
		DateOfTheReading = dateOfTheReading;
	}

	public SimpleStringProperty getTimeOfTheReading() {
		return timeOfTheReading;
	}

	public void setTimeOfTheReading(SimpleStringProperty timeOfTheReading) {
		this.timeOfTheReading = timeOfTheReading;
	}

	public SimpleDoubleProperty getGlucoseValue() {
		return GlucoseValue;
	}

	public void setGlucoseValue(SimpleDoubleProperty glucoseValue) {
		GlucoseValue = glucoseValue;
	}

	//public SimpleDoubleProperty getDosage() {
//		return dosage;
	//}

//	public void setDosage(SimpleDoubleProperty dosage) {
	//	this.dosage = dosage;
	//}

	//public SimpleStringProperty getCriticality() {
		//return criticality;
	//}

	//public void setCriticality(SimpleStringProperty criticality) {
		//this.criticality = criticality;
//	}

	
	
public PatientHistory(String DateOfTheReading,String timeOfTheReading,double GlucoseValue) {
	this.DateOfTheReading = new SimpleStringProperty(DateOfTheReading);
	this.timeOfTheReading = new SimpleStringProperty(timeOfTheReading);
	this.GlucoseValue = new SimpleDoubleProperty(GlucoseValue);
	//this.dosage = new SimpleDoubleProperty(dosage);
	//this.criticality = new SimpleStringProperty(criticality);
}
}
