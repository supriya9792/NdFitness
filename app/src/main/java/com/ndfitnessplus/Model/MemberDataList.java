package com.ndfitnessplus.Model;

public class MemberDataList {
    String ID , name ,Gender ,BirthDate ,Contact ,Address , Status, ExecutiveName;

    public MemberDataList() {
    }

    public MemberDataList(String ID, String name, String gender, String birthDate, String contact, String address, String status, String executiveName) {
        this.ID = ID;
        this.name = name;
        Gender = gender;
        BirthDate = birthDate;
        Contact = contact;
        Address = address;
        Status = status;
        ExecutiveName = executiveName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getExecutiveName() {
        return ExecutiveName;
    }

    public void setExecutiveName(String executiveName) {
        ExecutiveName = executiveName;
    }
}
