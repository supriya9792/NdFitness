package com.ndfitnessplus.Model;

public class StaffAttendanceList {
     public  String StaffID,Name,Contact,AttendanceDate,InTime,OutTime,AttendanceMode,Image;

    public StaffAttendanceList() {
    }

    public StaffAttendanceList(String staffID, String name, String contact, String attendanceDate, String inTime, String outTime, String attendanceMode) {
        StaffID = staffID;
        Name = name;
        Contact = contact;
        AttendanceDate = attendanceDate;
        InTime = inTime;
        OutTime = outTime;
        AttendanceMode = attendanceMode;
    }

    public String getStaffID() {
        return StaffID;
    }

    public void setStaffID(String staffID) {
        StaffID = staffID;
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

    public String getInTime() {
        return InTime;
    }

    public void setInTime(String inTime) {
        InTime = inTime;
    }

    public String getOutTime() {
        return OutTime;
    }

    public void setOutTime(String outTime) {
        OutTime = outTime;
    }

    public String getAttendanceMode() {
        return AttendanceMode;
    }

    public void setAttendanceMode(String attendanceMode) {
        AttendanceMode = attendanceMode;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
