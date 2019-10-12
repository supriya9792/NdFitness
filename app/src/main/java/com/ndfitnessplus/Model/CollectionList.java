package com.ndfitnessplus.Model;

import java.io.Serializable;

public class CollectionList implements Serializable {
    String  ID , name ,Contact,ReceiptDate ,PaymentType ,ExecutiveName, Paid, Balance,Email,
            InvoiceID,NextPaymentdate,ReceiptId,image,ReceiptType,Tax,PaymentDetails,ContactEncrypt;

    public CollectionList() {
    }

    public CollectionList(String ID, String name, String contact, String receiptDate, String paymentType, String executiveName, String rate,
                          String paid, String balance, String email, String invoiceID, String nextPaymentdate,String receiptId) {
        this.ID = ID;
        this.name = name;
        Contact = contact;
        ReceiptDate = receiptDate;
        PaymentType = paymentType;
        ExecutiveName = executiveName;

        Paid = paid;
        Balance = balance;
        Email = email;
        InvoiceID = invoiceID;
        NextPaymentdate = nextPaymentdate;
        ReceiptId=receiptId;
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

    public String getExecutiveName() {
        return ExecutiveName;
    }

    public void setExecutiveName(String executiveName) {
        ExecutiveName = executiveName;
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

    public String getNextPaymentdate() {
        return NextPaymentdate;
    }

    public void setNextPaymentdate(String nextPaymentdate) {
        NextPaymentdate = nextPaymentdate;
    }

    public String getReceiptId() {
        return ReceiptId;
    }

    public void setReceiptId(String receiptId) {
        ReceiptId = receiptId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReceiptType() {
        return ReceiptType;
    }

    public void setReceiptType(String receiptType) {
        ReceiptType = receiptType;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getPaymentDetails() {
        return PaymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        PaymentDetails = paymentDetails;
    }

    public String getContactEncrypt() {
        return ContactEncrypt;
    }

    public void setContactEncrypt(String contactEncrypt) {
        ContactEncrypt = contactEncrypt;
    }
}
