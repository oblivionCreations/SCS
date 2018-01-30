package application.Model;

public class PatientProfile {

	public String name;
	public String patientProfile;
	public int age;
	public String additionalDrugs;
	public int patientID;
	public String contactNo;
	public String address;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAdditionalDrugs() {
		return additionalDrugs;
	}

	public void setAdditionalDrugs(String additionalDrugs) {
		this.additionalDrugs = additionalDrugs;
	}

	public int getPatientID() {
		return patientID;
	}

	public void setPatientID(int patientID) {
		this.patientID = patientID;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}


	
	public PatientProfile(String name,int age,int patientID,String address,String additionalDrugs,String contactNo){
		this.name = name;
		this.address = address;
		this.age = age;
		this.additionalDrugs = additionalDrugs;
		this.patientID = patientID;
		this.contactNo = contactNo;
	}
	public PatientProfile() {
		
	}
}
