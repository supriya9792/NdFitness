package com.ndfitnessplus.Model;

public class ProductTrasanctionList {
    String ProdCode;

    String ProdName;

    String ProductFinalRate;

    String Quantity;

    String Rate;

    public ProductTrasanctionList(String prodCode, String prodName, String productFinalRate, String quantity, String rate) {
        ProdCode = prodCode;
        ProdName = prodName;
        ProductFinalRate = productFinalRate;
        Quantity = quantity;
        Rate = rate;
    }

    public ProductTrasanctionList() {
    }

    public String getProdCode() {
        return ProdCode;
    }

    public void setProdCode(String prodCode) {
        ProdCode = prodCode;
    }

    public String getProdName() {
        return ProdName;
    }

    public void setProdName(String prodName) {
        ProdName = prodName;
    }

    public String getProductFinalRate() {
        return ProductFinalRate;
    }

    public void setProductFinalRate(String productFinalRate) {
        ProductFinalRate = productFinalRate;
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
}
