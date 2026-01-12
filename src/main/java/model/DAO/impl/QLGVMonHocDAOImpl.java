package model.DAO.impl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import config.DBConnection;
import model.DAO.QLGVMonHocDAO;

public class QLGVMonHocDAOImpl implements QLGVMonHocDAO {

    private Connection conn;

    public QLGVMonHocDAOImpl() {
        conn = DBConnection.getConnection();
    }
    public List<Integer> getSubjectsByTeacher(String teacherID) {
        List<Integer> list = new ArrayList<>();
        try {
            String sql = "SELECT SubjectID FROM QLGVMonHoc WHERE TeacherID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, teacherID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getInt("SubjectID"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public void insert(String teacherID, int subjectID) {
        try {
            String sql = "INSERT INTO QLGVMonHoc (TeacherID, SubjectID) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, teacherID);
            ps.setInt(2, subjectID);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 
    public void deleteByTeacher(String teacherID) {
        try {
            String sql = "DELETE FROM QLGVMonHoc WHERE TeacherID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, teacherID);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
