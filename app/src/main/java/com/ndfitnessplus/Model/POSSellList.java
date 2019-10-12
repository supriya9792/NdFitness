package com.ndfitnessplus.Model;

import java.io.Serializable;

public class POSSellList implements Serializable {
    String InvoiceId,InvoiceDate,CustName,CustContact,ProductCode,ProductName,Quantity,Rate,ProdTotal,Discount,Tax,ProdFinalAmount,ProdBalance,PaidAmount,
    TotalAmount,SaleExecutive,PaymentType,PaymentDtl,ContactEncrypt;

    public POSSellList() {
    }

    public POSSellList(String invoiceId, String invoiceDate, String custName, String custContact, String productCode, String productName, String quantity, String rate, String prodTotal, String discount, String tax, String prodFinalAmount, String prodBalance, String paidAmount, String totalAmount, String saleExecutive, String paymentType, String paymentDtl) {
        InvoiceId = invoiceId;
        InvoiceDate = invoiceDate;
        CustName = custName;
        CustContact = custContact;
        ProductCode = productCode;
        ProductName = productName;
        Quantity = quantity;
        Rate = rate;
        ProdTotal = prodTotal;
        Discount = discount;
        Tax = tax;
        ProdFinalAmount = prodFinalAmount;
        ProdBalance = prodBalance;
        PaidAmount = paidAmount;
        TotalAmount = totalAmount;
        SaleExecutive = saleExecutive;
        PaymentType = paymentType;
        PaymentDtl = paymentDtl;
    }

    public String getInvoiceId() {
        return InvoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        InvoiceId = invoiceId;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
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

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getProdTotal() {
        return ProdTotal;
    }

    public void setProdTotal(String prodTotal) {
        ProdTotal = prodTotal;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getProdFinalAmount() {
        return ProdFinalAmount;
    }

    public void setProdFinalAmount(String prodFinalAmount) {
        ProdFinalAmount = prodFinalAmount;
    }

    public String getProdBalance() {
        return ProdBalance;
    }

    public void setProdBalance(String prodBalance) {
        ProdBalance = prodBalance;
    }

    public String getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        PaidAmount = paidAmount;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        TotalAmount = totalAmount;
    }

    public String getSaleExecutive() {
        return SaleExecutive;
    }

    public void setSaleExecutive(String saleExecutive) {
        SaleExecutive = saleExecutive;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public String getPaymentDtl() {
        return PaymentDtl;
    }

    public void setPaymentDtl(String paymentDtl) {
        PaymentDtl = paymentDtl;
    }

    public String getContactEncrypt() {
        return ContactEncrypt;
    }

    public void setContactEncrypt(String contactEncrypt) {
        ContactEncrypt = contactEncrypt;
    }
}
