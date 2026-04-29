//model class-store data
package com.example.java_lms_group_01.model.users;

import java.time.LocalDate;

public class TechnicalOfficer extends User implements TechnicalOfficerRole {
    //extra field(only tech officer)
    private String registrationNo;
    private String password;

    //default constructor(create empty object)
    public TechnicalOfficer() {
    }

    //constructor(create full object)
    public TechnicalOfficer(String userId, String firstName, String lastName, String email, String address,
                            String phoneNumber, LocalDate dateOfBirth, String gender,
                            String registrationNo, String password) {
        //parent class feilds intialize
        super(userId, firstName, lastName, email, address, phoneNumber, dateOfBirth, gender);
        //tec officer specific fields
        this.registrationNo = registrationNo;
        this.password = password;
    }

    //getter -registeration number get
    public String getRegistrationNo() {
        return registrationNo;
    }

    //setter- registeration number set
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    //getter
    public String getPassword() {
        return password;
    }

    //setter
    public void setPassword(String password) {
        this.password = password;
    }

    //object print display format
    @Override
    public String toString() {
        return "TechnicalOfficer{" +
                "registrationNo='" + registrationNo + '\'' +
                '}';
    }
}
