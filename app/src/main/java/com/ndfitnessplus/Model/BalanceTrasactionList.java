package com.ndfitnessplus.Model;

public class BalanceTrasactionList {
    String  ID , paymentDate , PaymentType, ExecutiveName,Paid, Balance,InvoiceID,ReceiptType;

    public BalanceTrasactionList() {
    }

    public BalanceTrasactionList(String ID, String paymentDate, String paymentType, String executiveName, String paid, String balance, String invoiceID, String receiptType) {
        this.ID = ID;
        this.paymentDate = paymentDate;
        PaymentType = paymentType;
        ExecutiveName = executiveName;
        Paid = paid;
        Balance = balance;
        InvoiceID = invoiceID;
        ReceiptType = receiptType;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
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

    public String getInvoiceID() {
        return InvoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        InvoiceID = invoiceID;
    }

    public String getReceiptType() {
        return ReceiptType;
    }

    public void setReceiptType(String receiptType) {
        ReceiptType = receiptType;
    }
}
