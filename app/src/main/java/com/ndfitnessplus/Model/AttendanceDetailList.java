package com.ndfitnessplus.Model;

public class AttendanceDetailList {
    String AttendanceDate,Time,AttendanceMode;

    public AttendanceDetailList(String attendanceDate, String time, String attendanceMode) {
        AttendanceDate = attendanceDate;
        Time = time;
        AttendanceMode = attendanceMode;
    }

    public AttendanceDetailList() {
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

    public String getAttendanceMode() {
        return AttendanceMode;
    }

    public void setAttendanceMode(String attendanceMode) {
        AttendanceMode = attendanceMode;
    }
}
