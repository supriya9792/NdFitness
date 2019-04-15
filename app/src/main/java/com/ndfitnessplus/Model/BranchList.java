package com.ndfitnessplus.Model;

public class BranchList {

    String branchName,contactNumber,email, branchId, dailyCollection,monthlyCollection,location,city;

    public BranchList() {
    }

    public BranchList(String branchName, String contactNumber, String email, String branchId, String dailyCollection, String monthlyCollection, String location, String city) {
        this.branchName = branchName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.branchId = branchId;
        this.dailyCollection = dailyCollection;
        this.monthlyCollection = monthlyCollection;
        this.location = location;
        this.city = city;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getDailyCollection() {
        return dailyCollection;
    }

    public void setDailyCollection(String dailyCollection) {
        this.dailyCollection = dailyCollection;
    }

    public String getMonthlyCollection() {
        return monthlyCollection;
    }

    public void setMonthlyCollection(String monthlyCollection) {
        this.monthlyCollection = monthlyCollection;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
