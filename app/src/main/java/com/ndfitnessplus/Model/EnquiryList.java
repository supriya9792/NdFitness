package com.ndfitnessplus.Model;

import java.io.Serializable;

public class EnquiryList implements Serializable {
    String ID , name ,Gender ,Contact ,Address ,ExecutiveName,Comment, NextFollowUpDate,image,CallResponse,Rating,followupdate,
            ContactEncrypt,Budget;

    public EnquiryList() {
    }

    public EnquiryList(String ID, String name, String gender, String contact, String address, String executiveName, String comment
            ,String image,String callResponse,String Rating,String followupdate) {
        this.ID = ID;
        this.name = name;
        Gender = gender;
        Contact = contact;
        Address = address;
        ExecutiveName = executiveName;
        Comment = comment;
        this.image = image;
        this.followupdate=followupdate;
        this.CallResponse=callResponse;
        this.Rating=Rating;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getExecutiveName() {
        return ExecutiveName;
    }

    public void setExecutiveName(String executiveName) {
        ExecutiveName = executiveName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getNextFollowUpDate() {
        return NextFollowUpDate;
    }

    public void setNextFollowUpDate(String nextFollowUpDate) {
        NextFollowUpDate = nextFollowUpDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCallResponse() {
        return CallResponse;
    }

    public void setCallResponse(String callResponse) {
        CallResponse = callResponse;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getFollowupdate() {
        return followupdate;
    }

    public void setFollowupdate(String followupdate) {
        this.followupdate = followupdate;
    }

    public String getContactEncrypt() {
        return ContactEncrypt;
    }

    public void setContactEncrypt(String contactEncrypt) {
        ContactEncrypt = contactEncrypt;
    }

    public String getBudget() {
        return Budget;
    }

    public void setBudget(String budget) {
        Budget = budget;
    }
}
