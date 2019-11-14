package com.ndfitnessplus.Model;

import java.io.Serializable;

public class MemberDataList implements Serializable {
    String ID , name ,Gender ,BirthDate ,Contact ,Occupation , Status, ExecutiveName,image,
            blodGroup,Email,FollowupType,RegistrationDate,ContactEncrypt,EndDate,FinalBalance;

    public MemberDataList() {
    }

    public MemberDataList(String ID, String name, String gender, String birthDate, String contact, String occupation, String status, String executiveName, String image, String blodGroup, String email) {
        this.ID = ID;
        this.name = name;
        Gender = gender;
        BirthDate = birthDate;
        Contact = contact;
        Occupation = occupation;
        Status = status;
        ExecutiveName = executiveName;
        this.image = image;
        this.blodGroup = blodGroup;
        Email = email;
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

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getOccupation() {
        return Occupation;
    }

    public void setOccupation(String occupation) {
        Occupation = occupation;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBlodGroup() {
        return blodGroup;
    }

    public void setBlodGroup(String blodGroup) {
        this.blodGroup = blodGroup;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFollowupType() {
        return FollowupType;
    }

    public void setFollowupType(String followupType) {
        FollowupType = followupType;
    }

    public String getRegistrationDate() {
        return RegistrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        RegistrationDate = registrationDate;
    }

    public String getContactEncrypt() {
        return ContactEncrypt;
    }

    public void setContactEncrypt(String contactEncrypt) {
        ContactEncrypt = contactEncrypt;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getFinalBalance() {
        return FinalBalance;
    }

    public void setFinalBalance(String finalBalance) {
        FinalBalance = finalBalance;
    }
}
