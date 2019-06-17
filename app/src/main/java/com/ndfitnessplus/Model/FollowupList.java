package com.ndfitnessplus.Model;

import java.io.Serializable;

public class FollowupList implements Serializable {
    String ID,name,ExecutiveName, FollowupDate, Comment ,FollowupType, Rating, CallRespond, Contact, NextFollowupDate,Image;

    public FollowupList() {
    }

    public FollowupList(String ID, String name, String executiveName, String followupDate, String comment,
                        String followupType, String rating, String callRespond, String contact, String nextFollowupDate) {
        this.ID = ID;
        this.name = name;
        ExecutiveName = executiveName;
        FollowupDate = followupDate;
        Comment = comment;
        FollowupType = followupType;
        Rating = rating;
        CallRespond = callRespond;
        Contact = contact;
        NextFollowupDate = nextFollowupDate;
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

    public String getExecutiveName() {
        return ExecutiveName;
    }

    public void setExecutiveName(String executiveName) {
        ExecutiveName = executiveName;
    }

    public String getFollowupDate() {
        return FollowupDate;
    }

    public void setFollowupDate(String followupDate) {
        FollowupDate = followupDate;
    }

    public String getFollowupType() {
        return FollowupType;
    }

    public void setFollowupType(String followupType) {
        FollowupType = followupType;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCallRespond() {
        return CallRespond;
    }

    public void setCallRespond(String callRespond) {
        CallRespond = callRespond;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getNextFollowupDate() {
        return NextFollowupDate;
    }

    public void setNextFollowupDate(String nextFollowupDate) {
        NextFollowupDate = nextFollowupDate;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
