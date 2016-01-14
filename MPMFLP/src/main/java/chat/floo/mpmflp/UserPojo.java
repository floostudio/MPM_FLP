package chat.floo.mpmflp;

public class UserPojo {
	
	
	String userid,firstname, lastname, profilepic, logintype, city, country, gender, status;
	//additional data mpm
	String phone,jabatan,membersince,dealerArea,dealerKota,dealerName,kodeDealerAHM,kodeDealerMPM;
	public boolean isSelected;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getJabatan() {
		return jabatan;
	}

	public void setJabatan(String jabatan) {
		this.jabatan = jabatan;
	}

	public String getMembersince() {
		return membersince;
	}

	public void setMembersince(String membersince) {
		this.membersince = membersince;
	}

	public String getDealerArea() {
		return dealerArea;
	}

	public void setDealerArea(String dealerArea) {
		this.dealerArea = dealerArea;
	}

	public String getDealerKota() {
		return dealerKota;
	}

	public void setDealerKota(String dealerKota) {
		this.dealerKota = dealerKota;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getKodeDealerAHM() {
		return kodeDealerAHM;
	}

	public void setKodeDealerAHM(String kodeDealerAHM) {
		this.kodeDealerAHM = kodeDealerAHM;
	}

	public String getKodeDealerMPM() {
		return kodeDealerMPM;
	}

	public void setKodeDealerMPM(String kodeDealerMPM) {
		this.kodeDealerMPM = kodeDealerMPM;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getProfilepic() {
		return profilepic;
	}

	public void setProfilepic(String profilepic) {
		this.profilepic = profilepic;
	}

	public String getLogintype() {
		return logintype;
	}

	public void setLogintype(String logintype) {
		this.logintype = logintype;
	}

	public UserPojo(String userid, String firstname, String lastname,
			String profilepic, String logintype) {
		super();
		this.userid = userid;
		this.firstname = firstname;
		this.lastname = lastname;
		this.profilepic = profilepic;
		this.logintype = logintype;
		
	}

	public UserPojo() {
		super();
	}
	
	

}
