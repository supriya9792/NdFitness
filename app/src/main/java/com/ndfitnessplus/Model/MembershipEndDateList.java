package com.ndfitnessplus.Model;

public class MembershipEndDateList {

    String ID , name ,Package ,StartDate ,EndDate ,Amount , CourseMemberType ,Status;

    public MembershipEndDateList() {
    }

    public MembershipEndDateList(String ID, String name, String aPackage, String startDate,
                                 String endDate, String amount, String courseMemberType, String status) {
        this.ID = ID;
        this.name = name;
        Package = aPackage;
        StartDate = startDate;
        EndDate = endDate;
        Amount = amount;
        CourseMemberType = courseMemberType;
        Status = status;
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

    public String getPackage() {
        return Package;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCourseMemberType() {
        return CourseMemberType;
    }

    public void setCourseMemberType(String courseMemberType) {
        CourseMemberType = courseMemberType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
