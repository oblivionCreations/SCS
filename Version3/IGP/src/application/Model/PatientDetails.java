package application.Model;

public class PatientDetails {

	public PatientProfile patientProfile;
	public DeviceConfiguration deviceConfiguration;
	
	public PatientProfile getPatientProfile() {
		return patientProfile;
	}

	public void setPatientProfile(PatientProfile patientProfile) {
		this.patientProfile = patientProfile;
	}

	public DeviceConfiguration getDeviceConfiguration() {
		return deviceConfiguration;
	}

	public void setDeviceConfiguration(DeviceConfiguration deviceConfiguration) {
		this.deviceConfiguration = deviceConfiguration;
	}

	
	
	
	public PatientDetails(PatientProfile patientProfile,PatientHistory patientHistory,DeviceConfiguration deviceConfiguration) {
		this.patientProfile = patientProfile;		
		this.deviceConfiguration = deviceConfiguration;
	}
	
	public PatientDetails() {
		
	}
}
