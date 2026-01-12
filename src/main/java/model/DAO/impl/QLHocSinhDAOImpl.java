package model.DAO.impl;

import config.DBConnection;
import model.DAO.QLHocSinhDAO;
import model.bean.QLHocSinh;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLHocSinhDAOImpl implements QLHocSinhDAO {
	public List<QLHocSinh> getAll() {
		List<QLHocSinh> list = new ArrayList<>();
		String sql = "SELECT * FROM QLHocSinh ORDER BY FullName ASC";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToStudent(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}


	public QLHocSinh getById(int id) {
		String sql = "SELECT * FROM QLHocSinh WHERE ID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToStudent(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public QLHocSinh getByStudentID(String studentID) {
		String sql = "SELECT * FROM QLHocSinh WHERE StudentID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, studentID);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToStudent(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void insert(QLHocSinh s) {
		String sql = "INSERT INTO QLHocSinh(StudentID, FullName, Birth, Gender, Address, Nation, Religion, StatusStudent, NumberPhone, Images, IsActive) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			fillPreparedStatement(ps, s);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(QLHocSinh s) {
		String sql = "UPDATE QLHocSinh SET StudentID=?, FullName=?, Birth=?, Gender=?, Address=?, Nation=?, Religion=?, StatusStudent=?, NumberPhone=?, Images=?, IsActive=? WHERE ID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			fillPreparedStatement(ps, s);
			ps.setInt(12, s.getID());
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void delete(int id) {
		String sql = "DELETE FROM QLHocSinh WHERE ID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void toggleStatus(int id) {
		QLHocSinh s = getById(id);
		if (s != null) {
			s.setIsActive(!s.isIsActive());
			update(s);
		}
	}

	private QLHocSinh mapResultSetToStudent(ResultSet rs) throws SQLException {
		QLHocSinh s = new QLHocSinh();
		s.setID(rs.getInt("ID"));
		s.setStudentID(rs.getString("StudentID"));
		s.setFullName(rs.getString("FullName"));
		s.setBirth(rs.getDate("Birth"));
		s.setGender(rs.getString("Gender"));
		s.setAddress(rs.getString("Address"));
		s.setNation(rs.getString("Nation"));
		s.setReligion(rs.getString("Religion"));
		s.setStatusStudent(rs.getString("StatusStudent"));
		s.setNumberPhone(rs.getString("NumberPhone"));
		s.setImages(rs.getString("Images"));
		s.setIsActive(rs.getBoolean("IsActive"));
		return s;
	}

	private void fillPreparedStatement(PreparedStatement ps, QLHocSinh s) throws SQLException {
		ps.setString(1, s.getStudentID());
		ps.setString(2, s.getFullName());
		ps.setDate(3, s.getBirth() != null ? new java.sql.Date(s.getBirth().getTime()) : null);
		ps.setString(4, s.getGender());
		ps.setString(5, s.getAddress());
		ps.setString(6, s.getNation());
		ps.setString(7, s.getReligion());
		ps.setString(8, s.getStatusStudent());
		ps.setString(9, s.getNumberPhone());
		ps.setString(10, s.getImages());
		ps.setBoolean(11, s.isIsActive());
	}

	public List<QLHocSinh> getByClass(Integer classId, Integer cohort) {
	    List<QLHocSinh> list = new ArrayList<>();
	    if (classId == null || cohort == null) return list; // tránh null pointer

	    String sql = """
	        SELECT hs.StudentID, hs.FullName
	        FROM QLHocSinh hs
	        JOIN QLHocSinhLopHoc lh ON hs.StudentID = lh.StudentID
	        JOIN QLLopHoc l ON lh.ClassID = l.ClassID
	        JOIN QLKhoaHoc kh ON l.CourseID = kh.CourseID
	        WHERE lh.ClassID = ? 
	          AND kh.Cohort = ? 
	          AND lh.IsActive = 1
	          AND l.IsActive = 1
	          AND kh.IsActive = 1
	        ORDER BY hs.FullName
	    """;

	    try (Connection c = DBConnection.getConnection();
	         PreparedStatement ps = c.prepareStatement(sql)) {

	        ps.setInt(1, classId);
	        ps.setInt(2, cohort);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                QLHocSinh hs = new QLHocSinh();
	                hs.setStudentID(rs.getString("StudentID"));
	                hs.setFullName(rs.getString("FullName"));
	                list.add(hs);
	            }
	        }

	    } catch (SQLException e) {

	        e.printStackTrace();
	    }

	    return list;
	}



}
