package org.example.studentmanagementsystem.services;
import org.example.studentmanagementsystem.models.Student;

import javax.management.Query;
import java.sql.*;
import java.util.*;

public class StudentRepo {
    public List<Student> getAllStudents()  {
        List<Student> students = new LinkedList<>();

        String query = "SELECT * FROM Students ";

        try (Connection conn = DbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                            rs.getString("Id"),
                            rs.getString("Name"),
                            rs.getDouble("Grade")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public void addStudent(Student std){
        String query = "INSERT INTO students VALUES (?, ?, ?)";

        try (Connection cn = DbConnector.getConnection(); PreparedStatement pds = cn.prepareStatement(query)){
            pds.setString(1, std.getId());
            pds.setString(2, std.getName());
            pds.setDouble(3, std.getGrade());

            pds.executeUpdate();
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }


}
