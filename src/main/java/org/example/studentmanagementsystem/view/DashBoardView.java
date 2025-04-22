package org.example.studentmanagementsystem.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.studentmanagementsystem.models.Student;
import org.example.studentmanagementsystem.services.Algorithms;
import org.example.studentmanagementsystem.services.StudentRepo;

import java.util.List;

public class DashBoardView {
    private final StudentRepo studentRepo;
    private final Algorithms algorithms;

    public DashBoardView(StudentRepo studentRepo, Algorithms algorithms) {
        this.studentRepo = studentRepo;
        this.algorithms = algorithms;
    }

    public VBox getDashBoard() {

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Student Performance Dashboard");
        titleLabel.getStyleClass().add("section-title");

        // Summary cards
        HBox summaryCards = createSummaryCards();

        // Grade distribution chart
        VBox chartContainer = createGradeDistributionChart();

        // Recent student performance
        VBox recentPerformance = createRecentPerformanceSection();

        mainLayout.getChildren().addAll(titleLabel, summaryCards, chartContainer, recentPerformance);

        return mainLayout;
    }

    private HBox createSummaryCards() {
        HBox container = new HBox(15);
        container.setPadding(new Insets(10));

        List<Student> students = studentRepo.getAllStudents();
        int totalStudents = students.size();
        double averageGrade = algorithms.getStatistics(students, false)[1];
        double highestGrade = algorithms.getStatistics(students, false)[4];

        // Total Students Card
        VBox studentsCard = createSummaryCard("Total Students", String.valueOf(totalStudents), "students-card");

        // Average Grade Card
        VBox averageCard = createSummaryCard("Average Grade", String.format("%.2f", averageGrade), "average-card");

        // Highest Grade Card
        VBox highestCard = createSummaryCard("Highest Grade", String.valueOf(highestGrade), "highest-card");

        // Performance Distribution Card
        // Map<String, Integer> performanceCount = countPerformanceCategories(students);

        int excellentCount = algorithms.getPerformance(students, true)[4];
        String excellentPercentage = String.format("%.1f%%", (excellentCount * 100.0) / totalStudents);
        VBox excellentCard = createSummaryCard("Excellent Students", excellentPercentage, "excellent-card");

        container.getChildren().addAll(studentsCard, averageCard, highestCard, excellentCard);
        return container;
    }

    private VBox createSummaryCard(String title, String value, String styleClass) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.getStyleClass().addAll("summary-card", styleClass);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-value");

        card.getChildren().addAll(valueLabel, titleLabel);
        HBox.setHgrow(card, Priority.ALWAYS);

        return card;
    }

    private VBox createGradeDistributionChart() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("chart-container");

        Label chartTitle = new Label("Grade Distribution");
        chartTitle.getStyleClass().add("subsection-title");

        // Creating the chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Grade Range");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Students");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Student Grade Distribution");

        // Create data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Students");

        // Get grade ranges
        List<Student> students = studentRepo.getAllStudents();
        int[] gradeRanges = algorithms.getPerformance(students, true);

        series.getData().add(new XYChart.Data<>("90-100", gradeRanges[4]));
        series.getData().add(new XYChart.Data<>("80-89", gradeRanges[3]));
        series.getData().add(new XYChart.Data<>("70-79", gradeRanges[2]));
        series.getData().add(new XYChart.Data<>("60-69", gradeRanges[1]));
        series.getData().add(new XYChart.Data<>("0-59", gradeRanges[0]));

        barChart.getData().add(series);

        container.getChildren().addAll(chartTitle, barChart);
        return container;
    }

    private VBox createRecentPerformanceSection() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("recent-performance");

        Label sectionTitle = new Label("Recent Student Performance");
        sectionTitle.getStyleClass().add("subsection-title");

        // Grid for displaying top 5 students
        GridPane topStudentsGrid = new GridPane();
        topStudentsGrid.setHgap(10);
        topStudentsGrid.setVgap(10);
        topStudentsGrid.setPadding(new Insets(10));
        return container;

    }
}