package com.ndfitnessplus.Model;

/**
 * Created by admin on 10/25/2017.
 */

public class Search_list {
    String CustName,CustContact,MemberId,Email,Gender;

    public Search_list() {
    }

    public Search_list(String custName, String custContact) {
        CustName = custName;
        CustContact = custContact;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getCustContact() {
        return CustContact;
    }

    public void setCustContact(String custContact) {
        CustContact = custContact;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
