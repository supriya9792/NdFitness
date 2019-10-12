package com.ndfitnessplus.Model;

import java.io.Serializable;

public class POSItemList  implements Serializable {
    String ProductCode,ProductName,ProductDisc,ProductImage,Quantity,Rate,PurchaseAmount,MaxDiscount,Tax,AutoId,ProductFinalRate;
    public boolean addtocart = false;
    String SelectedQuantity;
    public POSItemList() {
    }



    public POSItemList(String productCode, String productName, String productDisc, String productImage, String quantity, String rate, String purchaseAmount, String maxDiscount, String tax) {
        ProductCode = productCode;
        ProductName = productName;
        ProductDisc = productDisc;
        ProductImage = productImage;
        Quantity = quantity;
        Rate = rate;
        PurchaseAmount = purchaseAmount;
        MaxDiscount = maxDiscount;
        Tax = tax;
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

    public String getProductDisc() {
        return ProductDisc;
    }

    public void setProductDisc(String productDisc) {
        ProductDisc = productDisc;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
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

    public String getPurchaseAmount() {
        return PurchaseAmount;
    }

    public void setPurchaseAmount(String purchaseAmount) {
        PurchaseAmount = purchaseAmount;
    }

    public String getMaxDiscount() {
        return MaxDiscount;
    }

    public void setMaxDiscount(String maxDiscount) {
        MaxDiscount = maxDiscount;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getAutoId() {
        return AutoId;
    }

    public void setAutoId(String autoId) {
        AutoId = autoId;
    }
    public String getProductFinalRate() {
        return ProductFinalRate;
    }

    public void setProductFinalRate(String productFinalRate) {
        ProductFinalRate = productFinalRate;
    }

    public boolean isAddtocart() {
        return addtocart;
    }

    public void setAddtocart(boolean addtocart) {
        this.addtocart = addtocart;
    }

    public String getSelectedQuantity() {
        return SelectedQuantity;
    }

    public void setSelectedQuantity(String selectedQuantity) {
        SelectedQuantity = selectedQuantity;
    }
}
