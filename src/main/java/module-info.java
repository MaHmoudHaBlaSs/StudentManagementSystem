module org.example.studentmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.management;


    opens org.example.studentmanagementsystem to javafx.fxml;
    exports org.example.studentmanagementsystem;
}