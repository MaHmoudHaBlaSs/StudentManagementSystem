package org.example.studentmanagementsystem;
//package com.gradingsystem.view;

import java.io.File;
import java.nio.file.Files;


import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
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
import javafx.stage.Stage;
import org.example.studentmanagementsystem.services.StudentRepo;
import org.example.studentmanagementsystem.view.DashBoardView;

import java.util.List;

public class HelloApplication extends Application {

    private StudentRepo studentRepo;
    private Algorithms algorithms;
    private ObservableList<Student> studentObservableList;
    private TableView<Student> studentTable;
    Label statusLabel;
    BorderPane mainLayout;
    Stage stage;

    @Override
    public void start(Stage primaryStage) {
        // Initialize services
        this.stage = primaryStage;
        studentRepo = new StudentRepo();
        algorithms = new Algorithms();

        // Create main layout
        mainLayout = new BorderPane();

        // Create top navigation bar
        HBox navigationBar = createNavigationBar();
        mainLayout.setTop(navigationBar);

        // Create student table view
        showStudents();

        // Create scene and show stage
        Scene scene = new Scene(mainLayout, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());

        primaryStage.setTitle("Student Grade Sorting and Analysis System");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load students from database
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
        sortOptions.getItems().addAll("By Name", "By Grade");
        sortOptions.setValue("By Name");
        sortOptions.setOnAction(e -> sortStudents(sortOptions.getValue()));

        ComboBox<String> performanceFilter = new ComboBox<>();
        performanceFilter.getItems().addAll("All", "Excellent", "Very Good", "Good", "Average", "Below Average");
        performanceFilter.setValue("All");
        performanceFilter.setOnAction(e -> filterByPerformance(performanceFilter.getValue()));

        Button addStudentBtn = new Button("Add Student");
        addStudentBtn.getStyleClass().add("action-button");
        addStudentBtn.setOnAction(e -> showAddStudentDialog());

        Button importStudentsBtn = new Button("Import Data");
        importStudentsBtn.getStyleClass().add("action-button");
        importStudentsBtn.setOnAction(e -> showImportDataDialog());

        headerBox.getChildren().addAll(titleLabel, new Label("Sort:"), sortOptions,
                new Label("Filter:"), performanceFilter, addStudentBtn, importStudentsBtn);

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
        statusLabel = new Label("Total Students: 0");
        statusBar.getChildren().addAll(statusLabel);

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

        switch (sortMethod) {
            case "By Name":
                sortedStudents = algorithms.sortByName(studentList, 0, studentList.size()-1);
                break;
            case "By Grade":
                sortedStudents = algorithms.sortByGrade(studentList, 0, studentList.size()-1);
                break;
            default:
                sortedStudents = studentList;
        }

        studentObservableList.setAll(sortedStudents);

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
        statusLabel.setText("Total Students: "+studentObservableList.size());
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
    private void showImportDataDialog() {
        // Create a new dialog
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Import Student Data");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);

        // Create layout
        VBox dialogLayout = new VBox(15);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.getStyleClass().add("import-dialog");

        // Create title
        Label titleLabel = new Label("Import Student Data from JSON");
        titleLabel.getStyleClass().add("dialog-title");

        // File path field
        HBox filePathBox = new HBox(10);
        filePathBox.setAlignment(Pos.CENTER);

        TextField filePathField = new TextField();
        filePathField.setPromptText("No file selected");
        filePathField.setPrefWidth(300);
        filePathField.setEditable(false);

        Button browseButton = new Button("Browse");
        browseButton.getStyleClass().add("primary-button");

        filePathBox.getChildren().addAll(filePathField, browseButton);

        // Instructions
        Label instructionsLabel = new Label("Please select a JSON file containing student data to import.");
        instructionsLabel.getStyleClass().add("dialog-instructions");
        instructionsLabel.setWrapText(true);

        // Status label for feedback
        Label statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        // Action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button importButton = new Button("Import");
        importButton.getStyleClass().add("primary-button");
        importButton.setDisable(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");

        actionButtons.getChildren().addAll(cancelButton, importButton);

        // Add all components to dialog layout
        dialogLayout.getChildren().addAll(
                titleLabel,
                instructionsLabel,
                filePathBox,
                statusLabel,
                actionButtons
        );

        // Set up file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        // Browse button action
        browseButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(dialogStage);
            if (selectedFile != null) {
                filePathField.setText(selectedFile.getAbsolutePath());
                importButton.setDisable(false);
                statusLabel.setText("");
            }
        });

        // Import button action
        importButton.setOnAction(e -> {
            try {
                String filePath = filePathField.getText();
                File jsonFile = new File(filePath);

                // Read and parse JSON file
                String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()));

                // Use a more robust JSON library like Gson or Jackson in production
                // This is a simplified example using JsonParser from javax.json
                importStudentsFromJson(jsonContent);

                statusLabel.setText("Data imported successfully!");
                statusLabel.getStyleClass().removeAll("error-text");
                statusLabel.getStyleClass().add("success-text");

                // Close dialog after short delay
                PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                delay.setOnFinished(event -> dialogStage.close());
                delay.play();

            } catch (IOException ex) {
                statusLabel.setText("Error reading file: " + ex.getMessage());
                statusLabel.getStyleClass().removeAll("success-text");
                statusLabel.getStyleClass().add("error-text");
            } catch (Exception ex) {
                statusLabel.setText("Error importing data: " + ex.getMessage());
                statusLabel.getStyleClass().removeAll("success-text");
                statusLabel.getStyleClass().add("error-text");
            }
        });

        // Cancel button action
        cancelButton.setOnAction(e -> dialogStage.close());

        // Create scene and show dialog
        Scene dialogScene = new Scene(dialogLayout);
        dialogScene.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());

        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    // Helper method to import students from JSON

    private void importStudentsFromJson(String jsonContent) {
        try {
            // Use Jackson for parsing the basic JSON structure
            ObjectMapper mapper = new ObjectMapper();

            // First parse as generic list of maps
            List<Map<String, Object>> studentMaps = mapper.readValue(
                    jsonContent,
                    mapper.getTypeFactory().constructCollectionType(List.class, Map.class)
            );

            List<Student> importedStudents = new ArrayList<>();

            // Then manually construct Student objects from the parsed data
            for (Map<String, Object> studentMap : studentMaps) {
                String id = (String) studentMap.get("id");
                String name = (String) studentMap.get("name");

                // Handle grade which could be Integer or Double in JSON
                double grade;
                Object gradeObj = studentMap.get("grade");
                if (gradeObj instanceof Integer) {
                    grade = ((Integer) gradeObj).doubleValue();
                } else {
                    grade = ((Number) gradeObj).doubleValue();
                }

                // Create student and calculate performance
                Student student = new Student(id, name, grade);
                student.calculatePerformance();
                importedStudents.add(student);
            }

            // Add imported students to repository
            for (Student student : importedStudents) {
                studentRepo.addStudent(student);
            }

            loadStudents();
            System.out.println("Successfully imported " + importedStudents.size() + " students");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse JSON data: " + e.getMessage(), e);
        }
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
                    double grade = Double.parseDouble(gradeField.getText());

                    if (name.isEmpty()) {
                        showAlert("Name cannot be empty");
                        return null;
                    }

                    if (grade < 0 || grade > 100) {
                        showAlert("Grade must be between 0 and 100");
                        return null;
                    }

                    student.setName(name);
                    student.setGrade(grade);
                    studentRepo.editStudent(student);
                    return student;
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
                    studentRepo.deleteStudent(student);
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
        DashBoardView dashBoard = new DashBoardView(studentRepo, algorithms);
        VBox dashBoardView = dashBoard.getDashBoard(stage);
        mainLayout.setCenter(dashBoardView);
    }

    private void showStudents() {
        // Already showing students, no need to do anything
        VBox studentTableView = createStudentTableView();
        mainLayout.setCenter(studentTableView);
        loadStudents();
    }

    private void showAnalytics() {

        VBox analyticsLayout = createAnalyticsView();
        mainLayout.setCenter(analyticsLayout);
    }

    private VBox createAnalyticsView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("analytics-container");

        // Statistics section
        Label statsTitle = new Label("Grade Analytics");
        statsTitle.getStyleClass().add("section-title");

        VBox statsBox = new VBox(10);
        statsBox.getStyleClass().add("stats-box");
        List<Student> students = studentRepo.getAllStudents();

        double[] stats = algorithms.getStatistics(students, false);
        double averageGrade = stats[1];
        double highestGrade = stats[4];
        double lowestGrade = stats[3];
        double medianGrade = stats[0];

        Label avgLabel = new Label("Average Grade: " + String.format("%.2f", averageGrade));
        Label highLabel = new Label("Highest Grade: " + highestGrade);
        Label lowLabel = new Label("Lowest Grade: " + lowestGrade);
        Label medianLabel = new Label("Median Grade: " + String.format("%.2f", medianGrade));

        avgLabel.getStyleClass().add("stat-label");
        highLabel.getStyleClass().add("stat-label");
        lowLabel.getStyleClass().add("stat-label");
        medianLabel.getStyleClass().add("stat-label");

        statsBox.getChildren().addAll(avgLabel, highLabel, lowLabel, medianLabel);

        // Performance distribution section
        Label perfTitle = new Label("Performance Analysis");
        perfTitle.getStyleClass().add("section-title");

        VBox distributionBox = new VBox(15);
        distributionBox.getStyleClass().add("distribution-box");

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

        int totalStudents = students.size();
        double excellentPercentage = totalStudents > 0 ? (excellentCount * 100.0 / totalStudents) : 0;
        double veryGoodPercentage = totalStudents > 0 ? (veryGoodCount * 100.0 / totalStudents) : 0;
        double goodPercentage = totalStudents > 0 ? (goodCount * 100.0 / totalStudents) : 0;
        double averagePercentage = totalStudents > 0 ? (averageCount * 100.0 / totalStudents) : 0;
        double belowAveragePercentage = totalStudents > 0 ? (belowAverageCount * 100.0 / totalStudents) : 0;

        // Create performance bars
        HBox excellentRow = createPerformanceRow("Excellent (90-100)", excellentPercentage, "excellent-bar");
        HBox veryGoodRow = createPerformanceRow("Very Good (80-89)", veryGoodPercentage, "very-good-bar");
        HBox goodRow = createPerformanceRow("Good (70-79)", goodPercentage, "good-bar");
        HBox averageRow = createPerformanceRow("Average (60-69)", averagePercentage, "average-bar");
        HBox belowAverageRow = createPerformanceRow("Below Average (0-59)", belowAveragePercentage, "below-average-bar");

        distributionBox.getChildren().addAll(
                excellentRow,
                veryGoodRow,
                goodRow,
                averageRow,
                belowAverageRow
        );

        container.getChildren().addAll(statsTitle, statsBox, perfTitle, distributionBox);

        return container;
    }

    private HBox createPerformanceRow(String label, double percentage, String barStyleClass) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);

        Label categoryLabel = new Label(label);
        categoryLabel.setPrefWidth(150);
        categoryLabel.getStyleClass().add("category-label");

        StackPane barContainer = new StackPane();
        barContainer.setPrefWidth(400);
        barContainer.setMaxWidth(400);
        barContainer.getStyleClass().add("bar-background");

        StackPane bar = new StackPane();
        bar.setPrefHeight(20);
        bar.setPrefWidth(percentage * 4); // Scale to fit container (400px * percentage/100)
        bar.setMaxWidth(percentage * 4);
        bar.getStyleClass().add(barStyleClass);
        bar.setAlignment(Pos.CENTER_LEFT);

        barContainer.getChildren().add(bar);
        barContainer.setAlignment(Pos.CENTER_LEFT);

        Label percentLabel = new Label(String.format("%.0f%%", percentage));
        percentLabel.getStyleClass().add("percent-label");
        //HBox.setHgrow(Region.USE_PREF_SIZE, barContainer);

        row.getChildren().addAll(categoryLabel, barContainer, percentLabel);

        return row;
    }

    public static void main(String[] args) {
        launch(args);
    }
}