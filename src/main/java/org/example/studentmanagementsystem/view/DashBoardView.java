package org.example.studentmanagementsystem.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.studentmanagementsystem.models.Student;
import org.example.studentmanagementsystem.services.Algorithms;
import org.example.studentmanagementsystem.services.StudentRepo;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class DashBoardView {
    private final StudentRepo studentRepo;
    private final Algorithms algorithms;

    public DashBoardView(StudentRepo studentRepo, Algorithms algorithms) {
        this.studentRepo = studentRepo;
        this.algorithms = algorithms;
    }

    public VBox getDashBoard(Stage stage) {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // Top Bar with Title and Export Button
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setSpacing(10);

        Label titleLabel = new Label("Student Performance Dashboard");
        titleLabel.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportButton = new Button("Export to PDF");
        exportButton.getStyleClass().add("export-button");
        exportButton.setOnAction(e -> exportToPDF(stage));

        topBar.getChildren().addAll(titleLabel, spacer, exportButton);

        // Summary, Chart, and Performance Sections
        HBox summaryCards = createSummaryCards();
        VBox chartContainer = createGradeDistributionChart();
        VBox recentPerformance = createRecentPerformanceSection();

        mainLayout.getChildren().addAll(topBar, summaryCards, chartContainer, recentPerformance);

        return mainLayout;
    }


    private void exportDashboardAsPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("D:\\StudentDashboard.pdf"));
            document.open();

            document.add(new Paragraph("Student Performance Dashboard", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph(" "));

            List<Student> students = studentRepo.getAllStudents();
            int totalStudents = students.size();
            double averageGrade = algorithms.getStatistics(students, false)[1];
            double highestGrade = algorithms.getStatistics(students, false)[4];
            int[] gradeRanges = algorithms.getPerformance(students, true);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.addCell("Total Students");
            summaryTable.addCell(String.valueOf(totalStudents));
            summaryTable.addCell("Average Grade");
            summaryTable.addCell(String.format("%.2f", averageGrade));
            summaryTable.addCell("Highest Grade");
            summaryTable.addCell(String.valueOf(highestGrade));
            summaryTable.addCell("Excellent Students");
            summaryTable.addCell(String.format("%.1f%%", (gradeRanges[4] * 100.0) / totalStudents));

            document.add(summaryTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Grade Distribution", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));

            PdfPTable chartTable = new PdfPTable(2);
            chartTable.setWidthPercentage(70);
            chartTable.addCell("Grade Range");
            chartTable.addCell("Number of Students");
            chartTable.addCell("90-100");
            chartTable.addCell(String.valueOf(gradeRanges[4]));
            chartTable.addCell("80-89");
            chartTable.addCell(String.valueOf(gradeRanges[3]));
            chartTable.addCell("70-79");
            chartTable.addCell(String.valueOf(gradeRanges[2]));
            chartTable.addCell("60-69");
            chartTable.addCell(String.valueOf(gradeRanges[1]));
            chartTable.addCell("0-59");
            chartTable.addCell(String.valueOf(gradeRanges[0]));

            document.add(chartTable);

            document.close();
            System.out.println("PDF exported successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void exportToPDF(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Dashboard PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Title
                Font titleFont = new Font(18, Font.BOLD);
                Paragraph title = new Paragraph("Student Performance Dashboard\n\n", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                // Summary section
                List<Student> students = studentRepo.getAllStudents();
                double avg = algorithms.getStatistics(students, false)[1];
                double max = algorithms.getStatistics(students, false)[4];
                int excellent = algorithms.getPerformance(students, true)[4];
                int total = students.size();

                PdfPTable summaryTable = new PdfPTable(2);
                summaryTable.setWidthPercentage(100);
                summaryTable.setSpacingBefore(10f);
                summaryTable.setSpacingAfter(10f);

                summaryTable.addCell("Total Students");
                summaryTable.addCell(String.valueOf(total));
                summaryTable.addCell("Average Grade");
                summaryTable.addCell(String.format("%.2f", avg));
                summaryTable.addCell("Highest Grade");
                summaryTable.addCell(String.valueOf(max));
                summaryTable.addCell("Excellent Students");
                summaryTable.addCell(String.valueOf(excellent));

                document.add(summaryTable);

                // Grade Distribution
                int[] ranges = algorithms.getPerformance(students, true);
                PdfPTable chartTable = new PdfPTable(2);
                chartTable.setWidthPercentage(100);
                chartTable.setSpacingBefore(10f);
                chartTable.setSpacingAfter(10f);

                chartTable.addCell("Grade Range");
                chartTable.addCell("Number of Students");
                chartTable.addCell("90-100");
                chartTable.addCell(String.valueOf(ranges[4]));
                chartTable.addCell("80-89");
                chartTable.addCell(String.valueOf(ranges[3]));
                chartTable.addCell("70-79");
                chartTable.addCell(String.valueOf(ranges[2]));
                chartTable.addCell("60-69");
                chartTable.addCell(String.valueOf(ranges[1]));
                chartTable.addCell("0-59");
                chartTable.addCell(String.valueOf(ranges[0]));

                Paragraph chartTitle = new Paragraph("\nGrade Distribution\n\n", titleFont);
                chartTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(chartTitle);
                document.add(chartTable);

                document.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("PDF exported successfully to:\n" + file.getAbsolutePath());
                alert.showAndWait();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText("Error exporting PDF");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }


}