package org.example.studentmanagementsystem;
//package com.gradingsystem.view;

import java.sql.*;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.studentmanagementsystem.models.Student;
import org.example.studentmanagementsystem.services.Algorithms;

import java.io.IOException;
import java.util.*;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.studentmanagementsystem.services.StudentRepo;

import java.util.List;

public class HelloApplication extends Application {

    private StudentRepo studentRepo;
    private Algorithms algorithms;
    private ObservableList<Student> studentObservableList;
    private TableView<Student> studentTable;

    @Override
    public void start(Stage primaryStage) {
        // Initialize services
        studentRepo = new StudentRepo();
        algorithms = new Algorithms();

        // Create main layout
        BorderPane mainLayout = new BorderPane();

        // Create top navigation bar
        HBox navigationBar = createNavigationBar();
        mainLayout.setTop(navigationBar);

        // Create student table view
        VBox studentTableView = createStudentTableView();
        mainLayout.setCenter(studentTableView);

        // Create scene and show stage
        Scene scene = new Scene(mainLayout, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());

        primaryStage.setTitle("Student Grade Sorting and Analysis System");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load students from database
        loadStudents();
    }

    private HBox createNavigationBar() {
        HBox navigationBar = new HBox(10);
        navigationBar.setPadding(new Insets(10));
        navigationBar.getStyleClass().add("navigation-bar");

        Button dashboardBtn = new Button("Dashboard");
        dashboardBtn.getStyleClass().add("nav-button");
        dashboardBtn.setOnAction(e -> showDashboard());

        Button studentsBtn = new Button("Students");
        studentsBtn.getStyleClass().add("nav-button");
        studentsBtn.setOnAction(e -> showStudents());

        Button analyticsBtn = new Button("Analytics");
        analyticsBtn.getStyleClass().add("nav-button");
        analyticsBtn.setOnAction(e -> showAnalytics());

        navigationBar.getChildren().addAll(dashboardBtn, studentsBtn, analyticsBtn);

        return navigationBar;
    }

    private VBox createStudentTableView() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        // Create title and controls
        HBox headerBox = new HBox(10);
        Label titleLabel = new Label("Student Records");
        titleLabel.getStyleClass().add("section-title");

        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("QuickSort", "Bubble Sort", "Merge Sort");
        sortOptions.setValue("QuickSort");
        sortOptions.setOnAction(e -> sortStudents(sortOptions.getValue()));

        ComboBox<String> performanceFilter = new ComboBox<>();
        performanceFilter.getItems().addAll("All", "Excellent", "Very Good", "Good", "Average", "Below Average");
        performanceFilter.setValue("All");
        performanceFilter.setOnAction(e -> filterByPerformance(performanceFilter.getValue()));

        Button addStudentBtn = new Button("Add Student");
        addStudentBtn.getStyleClass().add("action-button");
        addStudentBtn.setOnAction(e -> showAddStudentDialog());

        headerBox.getChildren().addAll(titleLabel, new Label("Sort:"), sortOptions,
                new Label("Filter:"), performanceFilter, addStudentBtn);

        // Create table view
        studentTable = new TableView<>();
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ID column
        TableColumn<Student, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        // Name column
        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        // Grade column
        TableColumn<Student, Double> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(cellData -> cellData.getValue().gradeProperty().asObject());

        // Performance column
        TableColumn<Student, String> performanceColumn = new TableColumn<>("Performance");
        performanceColumn.setCellValueFactory(cellData -> cellData.getValue().performanceProperty());

        // Actions column with edit button
        TableColumn<Student, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.getStyleClass().add("edit-button");
                editButton.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    showEditStudentDialog(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        studentTable.getColumns().addAll(idColumn, nameColumn, gradeColumn, performanceColumn, actionsColumn);

        // Create status bar
        HBox statusBar = new HBox(10);
        Label statusLabel = new Label("Total Students: 0");
        Label averageGradeLabel = new Label("Average Grade: 0");
        statusBar.getChildren().addAll(statusLabel, averageGradeLabel);

        container.getChildren().addAll(headerBox, studentTable, statusBar);

        return container;
    }

    private void loadStudents() {
        List<Student> students = studentRepo.getAllStudents();
        studentObservableList = FXCollections.observableArrayList(students);
        studentTable.setItems(studentObservableList);
        updateStatistics();
    }

    private void sortStudents(String sortMethod) {
        if (studentObservableList == null || studentObservableList.isEmpty()) {
            return;
        }

        List<Student> sortedStudents;
        List<Student> studentList = new ArrayList<>(studentObservableList);
/*
        switch (sortMethod) {
            case "QuickSort":
                sortedStudents = algorithms.quickSort(studentList);
                break;
            case "Bubble Sort":
                sortedStudents = algorithms.bubbleSort(studentList);
                break;
            case "Merge Sort":
                sortedStudents = algorithms.mergeSort(studentList);
                break;
            default:
                sortedStudents = studentList;
        }

        studentObservableList.setAll(sortedStudents);
*/
    }

    private void filterByPerformance(String performance) {
        List<Student> allStudents = studentRepo.getAllStudents();

        if ("All".equals(performance)) {
            studentObservableList.setAll(allStudents);
        } else {
            List<Student> filteredStudents = new java.util.ArrayList<>();
            for (Student student : allStudents) {
                if (performance.equals(student.getPerformance())) {
                    filteredStudents.add(student);
                }
            }
            studentObservableList.setAll(filteredStudents);
        }

        updateStatistics();
    }

    private void updateStatistics() {
        // This should be updated to show statistics based on current visible students
        // You might want to call algorithms.getAverageGrade() and other statistical methods here
    }

    private void showAddStudentDialog() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");
        dialog.setHeaderText("Enter student information:");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField idField = new TextField();
        idField.setPromptText("Id");

        TextField gradeField = new TextField();
        gradeField.setPromptText("Grade (0-100)");

        content.getChildren().addAll(
                new Label("Id:"), idField,
                new Label("Name:"), nameField,
                new Label("Grade:"), gradeField
        );

        dialog.getDialogPane().setContent(content);

        // Convert the result to a student when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String id = idField.getText();
                    String name = nameField.getText();
                    int grade = Integer.parseInt(gradeField.getText());

                    if (id.isEmpty()) {
                        showAlert("Id cannot be empty");
                        return null;
                    }

                    if (name.isEmpty()) {
                        showAlert("Name cannot be empty");
                        return null;
                    }

                    if (grade < 0 || grade > 100) {
                        showAlert("Grade must be between 0 and 100");
                        return null;
                    }

                    Student newStudent = new Student(id, name, grade);
                    studentRepo.addStudent(newStudent);

                    // Performance will be set based on grade in the StudentRepo
                    return newStudent;
                } catch (NumberFormatException e) {
                    showAlert("Grade must be a number");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(student -> {
            if (student != null) {
                studentRepo.addStudent(student);
                loadStudents();
            }
        });
    }

    private void showEditStudentDialog(Student student) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Edit student information:");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, deleteButtonType, ButtonType.CANCEL);

        // Create the form fields
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField nameField = new TextField(student.getName());
        TextField gradeField = new TextField(String.valueOf(student.getGrade()));

        content.getChildren().addAll(
                new Label("ID: " + student.getId()),
                new Label("Name:"), nameField,
                new Label("Grade:"), gradeField,
                new Label("Performance: " + student.getPerformance())
        );

        dialog.getDialogPane().setContent(content);

        // Handle result conversion
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    int grade = Integer.parseInt(gradeField.getText());

                    if (name.isEmpty()) {
                        showAlert("Name cannot be empty");
                        return null;
                    }

                    if (grade < 0 || grade > 100) {
                        showAlert("Grade must be between 0 and 100");
                        return null;
                    }

                    Student updatedStudent = new Student(student.getId(), name, grade);
                    // Performance will be updated in the StudentRepo
                    return updatedStudent;
                } catch (NumberFormatException e) {
                    showAlert("Grade must be a number");
                    return null;
                }
            } else if (dialogButton == deleteButtonType) {
                // Return the original student to indicate deletion
                student.setMarkedForDeletion(true);
                return student;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != null) {
                if (result.isMarkedForDeletion()) {
                    // Delete the student
                    //studentRepo.deleteStudent(student.getId());
                } else {
                    // Update the student
                    //studentRepo.updateStudent(result);
                }
                loadStudents();
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showDashboard() {
        // TODO: Implement dashboard view
        System.out.println("Dashboard view not yet implemented");
    }

    private void showStudents() {
        // Already showing students, no need to do anything
    }

    private void showAnalytics() {
        // Create and show analytics window
        Stage analyticsStage = new Stage();
        analyticsStage.setTitle("Student Analytics");

        VBox analyticsLayout = createAnalyticsView();
        Scene analyticsScene = new Scene(analyticsLayout, 800, 600);

        analyticsStage.setScene(analyticsScene);
        analyticsStage.show();
    }

    private VBox createAnalyticsView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        Label titleLabel = new Label("Grade Analytics");
        titleLabel.getStyleClass().add("section-title");

        // Add statistics sections
        VBox statsBox = new VBox(10);
        List<Student> students = studentRepo.getAllStudents();

        double[] stats = algorithms.getStatistics(students, false);
        double averageGrade = stats[1];
        double highestGrade = stats[4];
        double lowestGrade = stats[3];
        double medianGrade = stats[0];

        statsBox.getChildren().addAll(
                new Label("Average Grade: " + String.format("%.2f", averageGrade)),
                new Label("Highest Grade: " + highestGrade),
                new Label("Lowest Grade: " + lowestGrade),
                new Label("Median Grade: " + String.format("%.2f", medianGrade))
        );

        // Create performance distribution
        VBox distributionBox = new VBox(10);
        Label distributionLabel = new Label("Performance Distribution");
        distributionLabel.getStyleClass().add("subsection-title");

        int excellentCount = 0, veryGoodCount = 0, goodCount = 0, averageCount = 0, belowAverageCount = 0;

        for (Student student : students) {
            switch (student.getPerformance()) {
                case "Excellent":
                    excellentCount++;
                    break;
                case "Very Good":
                    veryGoodCount++;
                    break;
                case "Good":
                    goodCount++;
                    break;
                case "Average":
                    averageCount++;
                    break;
                case "Below Average":
                    belowAverageCount++;
                    break;
            }
        }

        distributionBox.getChildren().addAll(
                distributionLabel,
                new Label("Excellent: " + excellentCount + " students"),
                new Label("Very Good: " + veryGoodCount + " students"),
                new Label("Good: " + goodCount + " students"),
                new Label("Average: " + averageCount + " students"),
                new Label("Below Average: " + belowAverageCount + " students")
        );

        container.getChildren().addAll(titleLabel, statsBox, distributionBox);

        return container;
    }

    public static void main(String[] args) {
        launch(args);
    }
}