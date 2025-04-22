module org.example.studentmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;
    requires com.fasterxml.jackson.databind;
    requires java.sql;


    opens org.example.studentmanagementsystem to javafx.fxml;
    exports org.example.studentmanagementsystem;
}