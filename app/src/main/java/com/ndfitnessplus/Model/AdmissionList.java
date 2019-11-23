package com.ndfitnessplus.Model;

import android.widget.EditText;

public class AdmissionList {
    String ID , name ,Gender ,Contact ,RegDate ,Address , Status ,ExecutiveName,packageInfo
            ,totalPaid,startDate,endDate,balance,session,totalFeeDue;

    public AdmissionList() {
    }

    public AdmissionList(String ID, String name, String gender, String contact, String regDate, String address, String status, String executiveName, String packageInfo, String totalPaid, String startDate, String endDate, String balance, String session, String totalFeeDue) {
        this.ID = ID;
        this.name = name;
        Gender = gender;
        Contact = contact;
        RegDate = regDate;
        Address = address;
        Status = status;
        ExecutiveName = executiveName;
        this.packageInfo = packageInfo;
        this.totalPaid = totalPaid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.balance = balance;
        this.session = session;
        this.totalFeeDue = totalFeeDue;
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

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getRegDate() {
        return RegDate;
    }

    public void setRegDate(String regDate) {
        RegDate = regDate;
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

    public String getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(String packageInfo) {
        this.packageInfo = packageInfo;
    }

    public String getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(String totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getTotalFeeDue() {
        return totalFeeDue;
    }

    public void setTotalFeeDue(String totalFeeDue) {
        this.totalFeeDue = totalFeeDue;
    }
}
