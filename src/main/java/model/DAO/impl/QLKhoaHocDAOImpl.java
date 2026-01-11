package model.DAO.impl;

import config.DBConnection;
import model.DAO.QLKhoaHocDAO;
import model.bean.QLKhoaHoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLKhoaHocDAOImpl implements QLKhoaHocDAO {

    public List<QLKhoaHoc> getAll() {
        List<QLKhoaHoc> list = new ArrayList<>();
        String sql = "SELECT CourseID, StartYear, EndYear, Cohort, IsActive FROM QLKhoaHoc ORDER BY CourseID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                QLKhoaHoc q = new QLKhoaHoc();
                q.setCourseID(rs.getInt("CourseID"));
                q.setStartYear(rs.getInt("StartYear"));
                q.setEndYear(rs.getInt("EndYear"));
                q.setCohort(rs.getInt("Cohort"));
                q.setActive(rs.getBoolean("IsActive"));
                list.add(q);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public QLKhoaHoc getById(int id) {
        String sql = "SELECT CourseID, StartYear, EndYear, Cohort, IsActive FROM QLKhoaHoc WHERE CourseID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                QLKhoaHoc q = new QLKhoaHoc();
                q.setCourseID(rs.getInt("CourseID"));
                q.setStartYear(rs.getInt("StartYear"));
                q.setEndYear(rs.getInt("EndYear"));
                q.setCohort(rs.getInt("Cohort"));
                q.setActive(rs.getBoolean("IsActive"));
                return q;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(QLKhoaHoc q) {
        String sql = "INSERT INTO QLKhoaHoc (StartYear, EndYear, Cohort, IsActive) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, q.getStartYear());
            ps.setInt(2, q.getEndYear());
            ps.setInt(3, q.getCohort());
            ps.setBoolean(4, q.isActive());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(QLKhoaHoc q) {
        String sql = "UPDATE QLKhoaHoc SET StartYear=?, EndYear=?, Cohort=?, IsActive=? WHERE CourseID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, q.getStartYear());
            ps.setInt(2, q.getEndYear());
            ps.setInt(3, q.getCohort());
            ps.setBoolean(4, q.isActive());
            ps.setInt(5, q.getCourseID());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM QLKhoaHoc WHERE CourseID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
