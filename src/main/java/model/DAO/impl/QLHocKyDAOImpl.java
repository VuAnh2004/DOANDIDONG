package model.DAO.impl;

import config.DBConnection;
import model.DAO.QLHocKyDAO;
import model.bean.QLHocKy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLHocKyDAOImpl implements QLHocKyDAO {


    public List<QLHocKy> getAll() {
        List<QLHocKy> list = new ArrayList<>();
        String sql = "SELECT SemesterID, SemesterName, SemesterCode, IsActive FROM QLHocKy ORDER BY SemesterID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                QLHocKy hk = new QLHocKy();
                hk.setSemesterId(rs.getInt("SemesterID"));
                hk.setSemesterName(rs.getString("SemesterName"));
                hk.setSemesterCode(rs.getString("SemesterCode"));
                hk.setActive(rs.getBoolean("IsActive"));
                list.add(hk);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public QLHocKy getById(int id) {
        String sql = "SELECT SemesterID, SemesterName, SemesterCode, IsActive FROM QLHocKy WHERE SemesterID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    QLHocKy hk = new QLHocKy();
                    hk.setSemesterId(rs.getInt("SemesterID"));
                    hk.setSemesterName(rs.getString("SemesterName"));
                    hk.setSemesterCode(rs.getString("SemesterCode"));
                    hk.setActive(rs.getBoolean("IsActive"));
                    return hk;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(QLHocKy hk) {
        String sql = "INSERT INTO QLHocKy (SemesterName, SemesterCode, IsActive) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hk.getSemesterName());
            ps.setString(2, hk.getSemesterCode());
            ps.setBoolean(3, hk.isActive());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(QLHocKy hk) {
        String sql = "UPDATE QLHocKy SET SemesterName=?, SemesterCode=?, IsActive=? WHERE SemesterID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hk.getSemesterName());
            ps.setString(2, hk.getSemesterCode());
            ps.setBoolean(3, hk.isActive());
            ps.setInt(4, hk.getSemesterId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM QLHocKy WHERE SemesterID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public List<QLHocKy> getSchoolYear(String schoolYear) {
		
		return null;
	}
	
}
