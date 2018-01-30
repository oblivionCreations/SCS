package application.Model;

public class DeviceConfiguration {

	
	public boolean Manual;
	public String nextAppointment;
	public int PatientID;
	
	
	public int getPatientID() {
		return PatientID;
	}

	public void setPatientID(int patientID) {
		PatientID = patientID;
	}

	public boolean isManual() {
		return Manual;
	}

	public void setManual(boolean manual) {
		Manual = manual;
	}

	public String getNextAppointment() {
		return nextAppointment;
	}

	public void setNextAppointment(String nextAppointment) {
		this.nextAppointment = nextAppointment;
	}
	
	public DeviceConfiguration(boolean Manual,String nextAppointment){
		this.Manual = Manual;
		this.nextAppointment = nextAppointment;
	}
	public DeviceConfiguration() {
		
	}
}
