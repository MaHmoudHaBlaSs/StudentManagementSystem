package org.example.studentmanagementsystem.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {
    Connection cn;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection( "jdbc:sqlite:students.sqlite");
    }
}
