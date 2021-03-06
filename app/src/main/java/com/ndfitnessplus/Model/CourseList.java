package com.ndfitnessplus.Model;

import java.io.Serializable;

public class CourseList implements Serializable {
    String  ID , name ,Contact,registrationDate ,PackageNameWithDS , StartToEndDate ,EndDate, ExecutiveName,image, Rate,Paid, Balance,Email,
            InvoiceID,PackageName,Tax,NextPaymentdate,financialYear,followuptype,Status,ContactEncrypt,CourseStatus,ReceiptDate,PaymentType,
            ReceiptId,ReceiptType,PaymentDetails,BalanceRuppe;

    public CourseList() {
    }

    public CourseList(String ID, String name, String contact, String registrationDate, String packageName, String startToEndDate, String executiveName, String image, String rate, String paid, String balance) {
        this.ID = ID;
        this.name = name;
        Contact = contact;
        this.registrationDate = registrationDate;
        PackageNameWithDS = packageName;
        StartToEndDate = startToEndDate;
        ExecutiveName = executiveName;
        this.image = image;
        Rate = rate;
        Paid = paid;
        Balance = balance;
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

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPackageNameWithDS() {
        return PackageNameWithDS;
    }

    public void setPackageNameWithDS(String packageNameWithDS) {
        PackageNameWithDS = packageNameWithDS;
    }

    public String getStartToEndDate() {
        return StartToEndDate;
    }

    public void setStartToEndDate(String startToEndDate) {
        StartToEndDate = startToEndDate;
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

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getPaid() {
        return Paid;
    }

    public void setPaid(String paid) {
        Paid = paid;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getInvoiceID() {
        return InvoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        InvoiceID = invoiceID;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getNextPaymentdate() {
        return NextPaymentdate;
    }

    public void setNextPaymentdate(String nextPaymentdate) {
        NextPaymentdate = nextPaymentdate;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getFollowuptype() {
        return followuptype;
    }

    public void setFollowuptype(String followuptype) {
        this.followuptype = followuptype;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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

    public String getCourseStatus() {
        return CourseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        CourseStatus = courseStatus;
    }


    public String getReceiptDate() {
        return ReceiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        ReceiptDate = receiptDate;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public String getReceiptId() {
        return ReceiptId;
    }

    public void setReceiptId(String receiptId) {
        ReceiptId = receiptId;
    }

    public String getReceiptType() {
        return ReceiptType;
    }

    public void setReceiptType(String receiptType) {
        ReceiptType = receiptType;
    }

    public String getPaymentDetails() {
        return PaymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        PaymentDetails = paymentDetails;
    }

    public String getBalanceRuppe() {
        return BalanceRuppe;
    }

    public void setBalanceRuppe(String balanceRuppe) {
        BalanceRuppe = balanceRuppe;
    }
}
