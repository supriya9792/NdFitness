package com.ndfitnessplus.Model;

public class PostDateChequeList {
    String ID ,name ,PaymentDate ,PaymentMode ,CheckNo ,Paid ,TaxType ,PaidWithTax ,ChequeExpiryDate ,Status, contact, recieptNumber;

    public PostDateChequeList() {
    }

    public PostDateChequeList(String ID, String name, String paymentDate, String paymentMode, String checkNo, String paid,
                              String taxType, String paidWithTax, String chequeExpiryDate, String status, String contact, String recieptNumber) {
        this.ID = ID;
        this.name = name;
        PaymentDate = paymentDate;
        PaymentMode = paymentMode;
        CheckNo = checkNo;
        Paid = paid;
        TaxType = taxType;
        PaidWithTax = paidWithTax;
        ChequeExpiryDate = chequeExpiryDate;
        Status = status;
        this.contact = contact;
        this.recieptNumber = recieptNumber;
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

    public String getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        PaymentDate = paymentDate;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public String getCheckNo() {
        return CheckNo;
    }

    public void setCheckNo(String checkNo) {
        CheckNo = checkNo;
    }

    public String getPaid() {
        return Paid;
    }

    public void setPaid(String paid) {
        Paid = paid;
    }

    public String getTaxType() {
        return TaxType;
    }

    public void setTaxType(String taxType) {
        TaxType = taxType;
    }

    public String getPaidWithTax() {
        return PaidWithTax;
    }

    public void setPaidWithTax(String paidWithTax) {
        PaidWithTax = paidWithTax;
    }

    public String getChequeExpiryDate() {
        return ChequeExpiryDate;
    }

    public void setChequeExpiryDate(String chequeExpiryDate) {
        ChequeExpiryDate = chequeExpiryDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRecieptNumber() {
        return recieptNumber;
    }

    public void setRecieptNumber(String recieptNumber) {
        this.recieptNumber = recieptNumber;
    }
}
