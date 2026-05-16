package model.DAO.impl;

import config.DBConnection;
import model.DAO.QLMonHocDAO;
import model.bean.QLMonHoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLMonHocDAOImpl implements QLMonHocDAO {


    public List<QLMonHoc> getAll() {
        List<QLMonHoc> list = new ArrayList<>();
        String sql = "SELECT SubjectID, SubjectName, NumberOfLesson, Semester, IsActive FROM QLMonHoc ORDER BY SubjectID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                QLMonHoc mh = new QLMonHoc();
                mh.setSubjectID(rs.getInt("SubjectID"));
                mh.setSubjectName(rs.getString("SubjectName"));
                mh.setNumberOfLesson(rs.getInt("NumberOfLesson"));
                mh.setSemester(rs.getString("Semester"));
                mh.setActive(rs.getBoolean("IsActive"));
                list.add(mh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public QLMonHoc getById(int id) {
        String sql = "SELECT SubjectID, SubjectName, NumberOfLesson, Semester, IsActive FROM QLMonHoc WHERE SubjectID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    QLMonHoc mh = new QLMonHoc();
                    mh.setSubjectID(rs.getInt("SubjectID"));
                    mh.setSubjectName(rs.getString("SubjectName"));
                    mh.setNumberOfLesson(rs.getInt("NumberOfLesson"));
                    mh.setSemester(rs.getString("Semester"));
                    mh.setActive(rs.getBoolean("IsActive"));
                    return mh;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void insert(QLMonHoc mh) {
        String sql = "INSERT INTO QLMonHoc (SubjectName, NumberOfLesson, Semester, IsActive) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mh.getSubjectName());
            ps.setInt(2, mh.getNumberOfLesson());
            ps.setString(3, mh.getSemester());
            ps.setBoolean(4, mh.isActive());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void update(QLMonHoc mh) {
        String sql = "UPDATE QLMonHoc SET SubjectName=?, NumberOfLesson=?, Semester=?, IsActive=? WHERE SubjectID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mh.getSubjectName());
            ps.setInt(2, mh.getNumberOfLesson());
            ps.setString(3, mh.getSemester());
            ps.setBoolean(4, mh.isActive());
            ps.setInt(5, mh.getSubjectID());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(int id) {
        String sql = "DELETE FROM QLMonHoc WHERE SubjectID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}