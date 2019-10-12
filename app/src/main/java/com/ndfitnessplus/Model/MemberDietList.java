package com.ndfitnessplus.Model;

public class MemberDietList {
    String MealName,MealTime,MealDisc;
    public boolean expanded = false;
    public boolean parent = false;

    // flag when item swiped
    public boolean swiped = false;

    public MemberDietList(String mealName, String mealTime, String mealDisc, boolean expanded, boolean parent, boolean swiped) {
        MealName = mealName;
        MealTime = mealTime;
        MealDisc = mealDisc;
        this.expanded = expanded;
        this.parent = parent;
        this.swiped = swiped;
    }

    public MemberDietList() {
    }

    public String getMealName() {
        return MealName;
    }

    public void setMealName(String mealName) {
        MealName = mealName;
    }

    public String getMealTime() {
        return MealTime;
    }

    public void setMealTime(String mealTime) {
        MealTime = mealTime;
    }

    public String getMealDisc() {
        return MealDisc;
    }

    public void setMealDisc(String mealDisc) {
        MealDisc = mealDisc;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public boolean isSwiped() {
        return swiped;
    }

    public void setSwiped(boolean swiped) {
        this.swiped = swiped;
    }
}
