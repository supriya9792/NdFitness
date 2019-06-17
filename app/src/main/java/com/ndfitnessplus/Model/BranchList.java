package com.ndfitnessplus.Model;

public class BranchList {

    String branchName,contactNumber,email, branchId, dailyCollection,monthlyCollection,image,city,Status,daysleft;

    public BranchList() {
    }

    public BranchList(String branchName, String contactNumber, String email, String branchId, String dailyCollection, String monthlyCollection,
                      String image, String city) {
        this.branchName = branchName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.branchId = branchId;
        this.dailyCollection = dailyCollection;
        this.monthlyCollection = monthlyCollection;
        this.image = image;
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


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDaysleft() {
        return daysleft;
    }

    public void setDaysleft(String daysleft) {
        this.daysleft = daysleft;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
