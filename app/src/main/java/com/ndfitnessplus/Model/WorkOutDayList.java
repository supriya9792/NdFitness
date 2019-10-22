package com.ndfitnessplus.Model;

public class WorkOutDayList {

    String Day,MemberId,Level;
    public boolean section = false;
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

    public boolean isSection() {
        return section;
    }

    public void setSection(boolean section) {
        this.section = section;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }
}
