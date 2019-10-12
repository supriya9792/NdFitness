package com.ndfitnessplus.Model;

import java.io.Serializable;

public class MeasurementList implements Serializable {
    String Measurement_Date,Weight,Height,Age,BMI,Fat,Neck,Shoulder,Chest,Arms_R,Arms_L,ForArms,Waist,Hips,
            Thigh_R,Thigh_L,Calf_R,Calf_L,NextFollowupDate,Executive_Name,MemberId,Name,Contact,ContactEncrypt;

    public MeasurementList() {

    }

    public MeasurementList(String measurement_Date, String weight, String height, String age, String BMI, String fat, String neck, String shoulder, String chest, String arms_R, String arms_L, String forArms, String waist, String hips, String thigh_R, String thigh_L, String calf_R, String calf_L, String nextFollowupDate, String executive_Name) {
        Measurement_Date = measurement_Date;
        Weight = weight;
        Height = height;
        Age = age;
        this.BMI = BMI;
        Fat = fat;
        Neck = neck;
        Shoulder = shoulder;
        Chest = chest;
        Arms_R = arms_R;
        Arms_L = arms_L;
        ForArms = forArms;
        Waist = waist;
        Hips = hips;
        Thigh_R = thigh_R;
        Thigh_L = thigh_L;
        Calf_R = calf_R;
        Calf_L = calf_L;
        NextFollowupDate = nextFollowupDate;
        Executive_Name = executive_Name;
    }

    public String getMeasurement_Date() {
        return Measurement_Date;
    }

    public void setMeasurement_Date(String measurement_Date) {
        Measurement_Date = measurement_Date;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getBMI() {
        return BMI;
    }

    public void setBMI(String BMI) {
        this.BMI = BMI;
    }

    public String getFat() {
        return Fat;
    }

    public void setFat(String fat) {
        Fat = fat;
    }

    public String getNeck() {
        return Neck;
    }

    public void setNeck(String neck) {
        Neck = neck;
    }

    public String getShoulder() {
        return Shoulder;
    }

    public void setShoulder(String shoulder) {
        Shoulder = shoulder;
    }

    public String getChest() {
        return Chest;
    }

    public void setChest(String chest) {
        Chest = chest;
    }

    public String getArms_R() {
        return Arms_R;
    }

    public void setArms_R(String arms_R) {
        Arms_R = arms_R;
    }

    public String getArms_L() {
        return Arms_L;
    }

    public void setArms_L(String arms_L) {
        Arms_L = arms_L;
    }

    public String getForArms() {
        return ForArms;
    }

    public void setForArms(String forArms) {
        ForArms = forArms;
    }

    public String getWaist() {
        return Waist;
    }

    public void setWaist(String waist) {
        Waist = waist;
    }

    public String getHips() {
        return Hips;
    }

    public void setHips(String hips) {
        Hips = hips;
    }

    public String getThigh_R() {
        return Thigh_R;
    }

    public void setThigh_R(String thigh_R) {
        Thigh_R = thigh_R;
    }

    public String getThigh_L() {
        return Thigh_L;
    }

    public void setThigh_L(String thigh_L) {
        Thigh_L = thigh_L;
    }

    public String getCalf_R() {
        return Calf_R;
    }

    public void setCalf_R(String calf_R) {
        Calf_R = calf_R;
    }

    public String getCalf_L() {
        return Calf_L;
    }

    public void setCalf_L(String calf_L) {
        Calf_L = calf_L;
    }

    public String getNextFollowupDate() {
        return NextFollowupDate;
    }

    public void setNextFollowupDate(String nextFollowupDate) {
        NextFollowupDate = nextFollowupDate;
    }

    public String getExecutive_Name() {
        return Executive_Name;
    }

    public void setExecutive_Name(String executive_Name) {
        Executive_Name = executive_Name;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
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

    public String getContactEncrypt() {
        return ContactEncrypt;
    }

    public void setContactEncrypt(String contactEncrypt) {
        ContactEncrypt = contactEncrypt;
    }
}
