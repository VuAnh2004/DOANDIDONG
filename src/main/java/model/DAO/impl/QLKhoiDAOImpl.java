package model.DAO.impl;

import config.DBConnection;
import model.DAO.QLKhoiDAO;
import model.bean.QLKhoi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLKhoiDAOImpl implements QLKhoiDAO {

  
    public List<QLKhoi> getAll() {
        List<QLKhoi> list = new ArrayList<>();
        String sql = "SELECT GradeLevelId, GradeName, Description, IsActive FROM QLKhoi ORDER BY GradeLevelId";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                QLKhoi q = new QLKhoi();
                q.setGradeLevelId(rs.getInt("GradeLevelId"));
                q.setGradeName(rs.getString("GradeName"));
                q.setDescription(rs.getString("Description"));
                q.setActive(rs.getBoolean("IsActive"));
                list.add(q);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    
    public QLKhoi getById(int id) {
        String sql = "SELECT GradeLevelId, GradeName, Description, IsActive FROM QLKhoi WHERE GradeLevelId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    QLKhoi q = new QLKhoi();
                    q.setGradeLevelId(rs.getInt("GradeLevelId"));
                    q.setGradeName(rs.getString("GradeName"));
                    q.setDescription(rs.getString("Description"));
                    q.setActive(rs.getBoolean("IsActive"));
                    return q;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void insert(QLKhoi q) {
        String sql = "INSERT INTO QLKhoi (GradeName, Description, IsActive) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getGradeName());
            ps.setString(2, q.getDescription());
            ps.setBoolean(3, q.isActive());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

  
    public void update(QLKhoi q) {
        String sql = "UPDATE QLKhoi SET GradeName=?, Description=?, IsActive=? WHERE GradeLevelId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getGradeName());
            ps.setString(2, q.getDescription());
            ps.setBoolean(3, q.isActive());
            ps.setInt(4, q.getGradeLevelId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
    public void delete(int id) {
        String sql = "DELETE FROM QLKhoi WHERE GradeLevelId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
