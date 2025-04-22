package org.example.studentmanagementsystem.models;
import javafx.beans.property.*;

public class Student {
    private StringProperty id;
    private StringProperty name;
    private DoubleProperty grade;
    private StringProperty performance;
    private boolean markedForDeletion;


    public Student(String id, String name, double grade) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.grade = new SimpleDoubleProperty(grade);
        performance = new SimpleStringProperty();
        calculatePerformance();

        this.markedForDeletion = false;
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getGrade() {
        return grade.get();
    }

    public DoubleProperty gradeProperty() {
        return grade;
    }

    public String getPerformance() {
        return performance.get();
    }

    public void setPerformance(String performance) {
        this.performance.set(performance);
    }
    public void setGrade(double grade){
        this.grade = new SimpleDoubleProperty(grade);
    }
    public void setName(String name){
        this.name = new SimpleStringProperty(name);
    }

    public StringProperty performanceProperty() {
        return performance;
    }

    // Used for handling deletion in dialog
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    // Calculate performance based on grade
    public void calculatePerformance() {
        double gradeValue = getGrade();
        if (gradeValue >= 90) {
            setPerformance("Excellent");
        } else if (gradeValue >= 80) {
            setPerformance("Very Good");
        } else if (gradeValue >= 70) {
            setPerformance("Good");
        } else if (gradeValue >= 60) {
            setPerformance("Average");
        } else {
            setPerformance("Below Average");
        }
    }
}