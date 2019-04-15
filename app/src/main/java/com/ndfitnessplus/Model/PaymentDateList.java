package com.ndfitnessplus.Model;

public class PaymentDateList {
    String ID , name ,PayDate ,TotalFeeDue ,TotalPaid ,Balance , PaymentMode , PayDetails ,Status ,ExecutiveName, followupDate;

    public PaymentDateList() {
    }

    public PaymentDateList(String ID, String name, String payDate, String totalFeeDue, String totalPaid, String balance,
                           String paymentMode, String payDetails, String status, String executiveName, String followupDate) {
        this.ID = ID;
        this.name = name;
        PayDate = payDate;
        TotalFeeDue = totalFeeDue;
        TotalPaid = totalPaid;
        Balance = balance;
        PaymentMode = paymentMode;
        PayDetails = payDetails;
        Status = status;
        ExecutiveName = executiveName;
        this.followupDate = followupDate;
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

    public String getPayDate() {
        return PayDate;
    }

    public void setPayDate(String payDate) {
        PayDate = payDate;
    }

    public String getTotalFeeDue() {
        return TotalFeeDue;
    }

    public void setTotalFeeDue(String totalFeeDue) {
        TotalFeeDue = totalFeeDue;
    }

    public String getTotalPaid() {
        return TotalPaid;
    }

    public void setTotalPaid(String totalPaid) {
        TotalPaid = totalPaid;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public String getPayDetails() {
        return PayDetails;
    }

    public void setPayDetails(String payDetails) {
        PayDetails = payDetails;
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

    public String getFollowupDate() {
        return followupDate;
    }

    public void setFollowupDate(String followupDate) {
        this.followupDate = followupDate;
    }
}
