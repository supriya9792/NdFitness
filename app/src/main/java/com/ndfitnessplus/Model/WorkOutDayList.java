package com.ndfitnessplus.Model;

public class WorkOutDayList {

    String Day,MemberId;

    public WorkOutDayList(String day) {
        Day = day;
    }

    public WorkOutDayList() {
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }
}
