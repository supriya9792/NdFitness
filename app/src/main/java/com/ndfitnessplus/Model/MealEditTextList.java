package com.ndfitnessplus.Model;

import android.widget.EditText;

public class MealEditTextList {
    EditText inputTime,inputMeal,inputMessage;

    public MealEditTextList() {
    }

    public MealEditTextList(EditText inputTime, EditText inputMeal, EditText inputMessage) {
        this.inputTime = inputTime;
        this.inputMeal = inputMeal;
        this.inputMessage = inputMessage;
    }

    public EditText getInputTime() {
        return inputTime;
    }

    public void setInputTime(EditText inputTime) {
        this.inputTime = inputTime;
    }

    public EditText getInputMeal() {
        return inputMeal;
    }

    public void setInputMeal(EditText inputMeal) {
        this.inputMeal = inputMeal;
    }

    public EditText getInputMessage() {
        return inputMessage;
    }

    public void setInputMessage(EditText inputMessage) {
        this.inputMessage = inputMessage;
    }
}
