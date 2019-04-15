package com.ndfitnessplus.Model;

public class ExpensesList {
    String ttl_of_expenses,expenses_group,payment_dtl,amount,executive_name,payment_type,expenses_date,disc,expensesId;

    public ExpensesList() {
    }

    public ExpensesList(String ttl_of_expenses, String expenses_group, String payment_dtl, String amount, String executive_name, String payment_type, String expenses_date, String disc) {
        this.ttl_of_expenses = ttl_of_expenses;
        this.expenses_group = expenses_group;
        this.payment_dtl = payment_dtl;
        this.amount = amount;
        this.executive_name = executive_name;
        this.payment_type = payment_type;
        this.expenses_date = expenses_date;
        this.disc = disc;
    }

    public String getExpensesId() {
        return expensesId;
    }

    public void setExpensesId(String expensesId) {
        this.expensesId = expensesId;
    }

    public String getTtl_of_expenses() {
        return ttl_of_expenses;
    }

    public void setTtl_of_expenses(String ttl_of_expenses) {
        this.ttl_of_expenses = ttl_of_expenses;
    }

    public String getExpenses_group() {
        return expenses_group;
    }

    public void setExpenses_group(String expenses_group) {
        this.expenses_group = expenses_group;
    }

    public String getPayment_dtl() {
        return payment_dtl;
    }

    public void setPayment_dtl(String payment_dtl) {
        this.payment_dtl = payment_dtl;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExecutive_name() {
        return executive_name;
    }

    public void setExecutive_name(String executive_name) {
        this.executive_name = executive_name;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getExpenses_date() {
        return expenses_date;
    }

    public void setExpenses_date(String expenses_date) {
        this.expenses_date = expenses_date;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }
}
