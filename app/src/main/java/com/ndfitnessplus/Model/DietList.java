package com.ndfitnessplus.Model;

public class DietList {
    String Advoice;

    String Charges;

    String Contact;

    String DietEndDate;

    String DietId;

    String DietStartDate;

    String DietStartToEndDate;

    String DietitionName;

    String MemberID;

    String Name;

    String Paydetails;

    String Purpose;
    String ContactEncrypt;

    public DietList() {}

    public DietList(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10) {
        this.DietId = paramString1;
        this.Name = paramString2;
        this.Contact = paramString3;
        this.Purpose = paramString4;
        this.DietStartDate = paramString5;
        this.DietEndDate = paramString6;
        this.Advoice = paramString7;
        this.Charges = paramString8;
        this.Paydetails = paramString9;
        this.DietitionName = paramString10;
    }

    public String getAdvoice() { return this.Advoice; }

    public String getCharges() { return this.Charges; }

    public String getContact() { return this.Contact; }

    public String getDietEndDate() { return this.DietEndDate; }

    public String getDietId() { return this.DietId; }

    public String getDietStartDate() { return this.DietStartDate; }

    public String getDietStartToEndDate() { return this.DietStartToEndDate; }

    public String getDietitionName() { return this.DietitionName; }

    public String getMemberID() { return this.MemberID; }

    public String getName() { return this.Name; }

    public String getPaydetails() { return this.Paydetails; }

    public String getPurpose() { return this.Purpose; }

    public void setAdvoice(String paramString) { this.Advoice = paramString; }

    public void setCharges(String paramString) { this.Charges = paramString; }

    public void setContact(String paramString) { this.Contact = paramString; }

    public void setDietEndDate(String paramString) { this.DietEndDate = paramString; }

    public void setDietId(String paramString) { this.DietId = paramString; }

    public void setDietStartDate(String paramString) { this.DietStartDate = paramString; }

    public void setDietStartToEndDate(String paramString) { this.DietStartToEndDate = paramString; }

    public void setDietitionName(String paramString) { this.DietitionName = paramString; }

    public void setMemberID(String paramString) { this.MemberID = paramString; }

    public void setName(String paramString) { this.Name = paramString; }

    public void setPaydetails(String paramString) { this.Paydetails = paramString; }

    public void setPurpose(String paramString) { this.Purpose = paramString; }

    public String getContactEncrypt() {
        return ContactEncrypt;
    }

    public void setContactEncrypt(String contactEncrypt) {
        ContactEncrypt = contactEncrypt;
    }
}
