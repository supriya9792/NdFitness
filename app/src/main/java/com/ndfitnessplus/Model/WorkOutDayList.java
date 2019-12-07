package com.ndfitnessplus.Model;

import java.io.Serializable;

public class WorkOutDayList implements Serializable {

    String Day,MemberId,Level,ExerciseId,AssignDate,MemberName,MemberContact,EmailId,MemberImage,InstructorName,EncryptContact;
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

    public String getExerciseId() {
        return ExerciseId;
    }

    public void setExerciseId(String exerciseId) {
        ExerciseId = exerciseId;
    }

    public String getAssignDate() {
        return AssignDate;
    }

    public void setAssignDate(String assignDate) {
        AssignDate = assignDate;
    }

    public String getMemberName() {
        return MemberName;
    }

    public void setMemberName(String memberName) {
        MemberName = memberName;
    }

    public String getMemberContact() {
        return MemberContact;
    }

    public void setMemberContact(String memberContact) {
        MemberContact = memberContact;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getMemberImage() {
        return MemberImage;
    }

    public void setMemberImage(String memberImage) {
        MemberImage = memberImage;
    }

    public String getInstructorName() {
        return InstructorName;
    }

    public void setInstructorName(String instructorName) {
        InstructorName = instructorName;
    }

    public String getEncryptContact() {
        return EncryptContact;
    }

    public void setEncryptContact(String encryptContact) {
        EncryptContact = encryptContact;
    }
}
