package com.ndfitnessplus.Model;

import java.io.Serializable;

public class AttendanceList implements Serializable {
    String MemberID,Name,Contact,AttendanceDate,Time,Balance,PackageName,ExpiryDate,Status,ExecutiveName,AttendanceMode,ContactEncrypt;

    public AttendanceList(String memberID, String name, String contact, String attendanceDate, String time, String balance, String packageName, String expiryDate, String status) {
        MemberID = memberID;
        Name = name;
        Contact = contact;
        AttendanceDate = attendanceDate;
        Time = time;
        Balance = balance;
        PackageName = packageName;
        ExpiryDate = expiryDate;
        Status = status;
    }

    public AttendanceList() {
    }

    public String getMemberID() {
        return MemberID;
    }

    public void setMemberID(String memberID) {
        MemberID = memberID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getAttendanceDate() {
        return AttendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        AttendanceDate = attendanceDate;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
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

    public String getAttendanceMode() {
        return AttendanceMode;
    }

    public void setAttendanceMode(String attendanceMode) {
        AttendanceMode = attendanceMode;
    }

    public String getContactEncrypt() {
        return ContactEncrypt;
    }

    public void setContactEncrypt(String contactEncrypt) {
        ContactEncrypt = contactEncrypt;
    }
}
