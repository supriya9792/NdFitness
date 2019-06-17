package com.ndfitnessplus.Model;

public class StaffBirthdayList {
    String ID , name ,Gender ,BirthDate ,Contact ,Address , Designation,JoiningDate;

    public StaffBirthdayList() {
    }

    public StaffBirthdayList(String ID, String name, String gender, String birthDate, String contact, String address, String designation) {
        this.ID = ID;
        this.name = name;
        Gender = gender;
        BirthDate = birthDate;
        Contact = contact;
        Address = address;
        Designation = designation;
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

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public String getJoiningDate() {
        return JoiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        JoiningDate = joiningDate;
    }
}
