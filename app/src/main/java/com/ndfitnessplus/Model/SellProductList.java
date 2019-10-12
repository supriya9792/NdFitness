package com.ndfitnessplus.Model;

public class SellProductList {
    String ProductCode,ProductName,Rate,Quantity,Discount,ProdFinalRate;

    public SellProductList(String productCode, String productName, String rate, String quantity, String discount, String prodFinalRate) {
        ProductCode = productCode;
        ProductName = productName;
        Rate = rate;
        Quantity = quantity;
        Discount = discount;
        ProdFinalRate = prodFinalRate;
    }

    public SellProductList() {
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

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getProdFinalRate() {
        return ProdFinalRate;
    }

    public void setProdFinalRate(String prodFinalRate) {
        ProdFinalRate = prodFinalRate;
    }
}
