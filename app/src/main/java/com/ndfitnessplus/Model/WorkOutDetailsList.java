package com.ndfitnessplus.Model;

import java.io.Serializable;

public class WorkOutDetailsList implements Serializable {

    String BodyPart,WorkoutName,WorkoutImage,Discription,VideoLink,Set,Repitation,Time,Weight,InstructorName,PlanName,SetAndRepitations,
    Day,LevelName;

    public WorkOutDetailsList() {
    }

    public WorkOutDetailsList(String bodyPart, String workoutName, String workoutImage, String discription, String videoLink, String set, String repitation, String time, String weight, String instructorName, String planName) {
        BodyPart = bodyPart;
        WorkoutName = workoutName;
        WorkoutImage = workoutImage;
        Discription = discription;
        VideoLink = videoLink;
        Set = set;
        Repitation = repitation;
        Time = time;
        Weight = weight;
        InstructorName = instructorName;
        PlanName = planName;
    }

    public String getBodyPart() {
        return BodyPart;
    }

    public void setBodyPart(String bodyPart) {
        BodyPart = bodyPart;
    }

    public String getWorkoutName() {
        return WorkoutName;
    }

    public void setWorkoutName(String workoutName) {
        WorkoutName = workoutName;
    }

    public String getWorkoutImage() {
        return WorkoutImage;
    }

    public void setWorkoutImage(String workoutImage) {
        WorkoutImage = workoutImage;
    }

    public String getDiscription() {
        return Discription;
    }

    public void setDiscription(String discription) {
        Discription = discription;
    }

    public String getVideoLink() {
        return VideoLink;
    }

    public void setVideoLink(String videoLink) {
        VideoLink = videoLink;
    }

    public String getSet() {
        return Set;
    }

    public void setSet(String set) {
        Set = set;
    }

    public String getRepitation() {
        return Repitation;
    }

    public void setRepitation(String repitation) {
        Repitation = repitation;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getInstructorName() {
        return InstructorName;
    }

    public void setInstructorName(String instructorName) {
        InstructorName = instructorName;
    }

    public String getPlanName() {
        return PlanName;
    }

    public void setPlanName(String planName) {
        PlanName = planName;
    }

    public String getSetAndRepitations() {
        return SetAndRepitations;
    }

    public void setSetAndRepitations(String setAndRepitations) {
        SetAndRepitations = setAndRepitations;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getLevelName() {
        return LevelName;
    }

    public void setLevelName(String levelName) {
        LevelName = levelName;
    }
}
