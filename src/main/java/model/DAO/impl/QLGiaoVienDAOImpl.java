package model.DAO.impl;

import config.DBConnection;
import model.DAO.QLGiaoVienDAO;
import model.bean.QLGiaoVien;
import model.bean.QLMonHoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Cần thiết cho việc ánh xạ tên môn học

public class QLGiaoVienDAOImpl implements QLGiaoVienDAO {

	private QLGiaoVien mapResultSetToGiaoVien(ResultSet rs) throws SQLException {
		QLGiaoVien gv = new QLGiaoVien();
		gv.setID(rs.getInt("ID"));
		gv.setTeacherID(rs.getString("TeacherID"));
		gv.setFullName(rs.getString("FullName"));
		gv.setBirth(rs.getDate("Birth"));
		gv.setGender(rs.getString("Gender"));
		gv.setAddress(rs.getString("Address"));
		gv.setStatusTeacher(rs.getString("StatusTeacher"));
		gv.setCCCD(rs.getString("CCCD"));
		gv.setNation(rs.getString("Nation"));
		gv.setReligion(rs.getString("Religion"));
		gv.setGroupDV(rs.getString("GroupDV"));
		gv.setNumberPhone(rs.getString("NumberPhone"));
		gv.setNumberBHXH(rs.getString("NumberBHXH"));
		gv.setIsActive(rs.getBoolean("IsActive"));
		gv.setImages(rs.getString("Images"));
		return gv;
	}
	private void insertTeacherSubjectsInternal(Connection conn, String teacherID, List<Integer> subjectIDs)
			throws SQLException {
		String sql = "INSERT INTO QLGVMonHoc (TeacherID, SubjectID) VALUES (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer id : subjectIDs) {
				ps.setString(1, teacherID);
				ps.setInt(2, id);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}
	private void deleteSubjectsByTeacherIDInternal(Connection conn, String teacherID) throws SQLException {
		String sql = "DELETE FROM QLGVMonHoc WHERE TeacherID=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, teacherID);
			ps.executeUpdate();
		}
	}
	public List<QLGiaoVien> getAll() {
		List<QLGiaoVien> list = new ArrayList<>();
		String sql = "SELECT * FROM QLGiaoVien ORDER BY FullName";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			
			while (rs.next()) {
				list.add(mapResultSetToGiaoVien(rs));
			}
			for (QLGiaoVien gv : list) {
				List<QLMonHoc> subjects = getSubjectsByTeacherID(gv.getTeacherID());
				List<String> subjectNames = subjects.stream().map(QLMonHoc::getSubjectName)
						.collect(Collectors.toList());
				gv.setSubjectNames(subjectNames);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	public QLGiaoVien getById(int id) {
	    String sql = "SELECT * FROM QLGiaoVien WHERE ID=?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, id); 
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return mapResultSetToGiaoVien(rs);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public void insert(QLGiaoVien gv) {
		String sqlGV = "INSERT INTO QLGiaoVien (TeacherID, FullName, Birth, Gender, Address, StatusTeacher, CCCD, Nation, Religion, GroupDV, NumberPhone, NumberBHXH, Images, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = null;

		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);

			try (PreparedStatement ps = conn.prepareStatement(sqlGV)) {
				ps.setString(1, gv.getTeacherID());
				ps.setString(2, gv.getFullName());
				ps.setDate(3, gv.getBirth());
				ps.setString(4, gv.getGender());
				ps.setString(5, gv.getAddress());
				ps.setString(6, gv.getStatusTeacher());
				ps.setString(7, gv.getCCCD());
				ps.setString(8, gv.getNation());
				ps.setString(9, gv.getReligion());
				ps.setString(10, gv.getGroupDV());
				ps.setString(11, gv.getNumberPhone());
				ps.setString(12, gv.getNumberBHXH());
				ps.setString(13, gv.getImages());
				ps.setBoolean(14, gv.isIsActive());
				ps.executeUpdate();
			}

			if (gv.getSubjectIDs() != null && !gv.getSubjectIDs().isEmpty()) {
				insertTeacherSubjectsInternal(conn, gv.getTeacherID(), gv.getSubjectIDs());
			}

			conn.commit();
		} catch (SQLException e) {
			System.err.println("--- LỖI INSERT QLGiaoVien ---");
			System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
			System.err.println("Database Error Message: " + e.getMessage());
			e.printStackTrace();

			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw new RuntimeException("Thêm giáo viên thất bại do lỗi cơ sở dữ liệu.", e);
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void update(QLGiaoVien gv) {
		String sqlGV = "UPDATE QLGiaoVien SET FullName=?, Birth=?, Gender=?, Address=?, StatusTeacher=?, CCCD=?, Nation=?, Religion=?, GroupDV=?, NumberPhone=?, NumberBHXH=?, Images=?, IsActive=? WHERE TeacherID=?";
		Connection conn = null;

		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			try (PreparedStatement ps = conn.prepareStatement(sqlGV)) {
				ps.setString(1, gv.getFullName());
				ps.setDate(2, gv.getBirth());
				ps.setString(3, gv.getGender());
				ps.setString(4, gv.getAddress());
				ps.setString(5, gv.getStatusTeacher());
				ps.setString(6, gv.getCCCD());
				ps.setString(7, gv.getNation());
				ps.setString(8, gv.getReligion());
				ps.setString(9, gv.getGroupDV());
				ps.setString(10, gv.getNumberPhone());
				ps.setString(11, gv.getNumberBHXH());
				ps.setString(12, gv.getImages());
				ps.setBoolean(13, gv.isIsActive());
				ps.setString(14, gv.getTeacherID());
				ps.executeUpdate();
			}
			List<Integer> oldSubjects = getSubjectsByTeacherID(gv.getTeacherID()).stream().map(m -> m.getSubjectID())
					.collect(Collectors.toList());
			List<Integer> newSubjects = gv.getSubjectIDs() != null ? gv.getSubjectIDs() : oldSubjects;

			List<Integer> toDelete = oldSubjects.stream().filter(id -> !newSubjects.contains(id))
					.collect(Collectors.toList());

			List<Integer> toAdd = newSubjects.stream().filter(id -> !oldSubjects.contains(id))
					.collect(Collectors.toList());

			deleteTeacherSubjectsByIDs(conn, gv.getTeacherID(), toDelete);
			insertTeacherSubjectsInternal(conn, gv.getTeacherID(), toAdd);

			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw new RuntimeException("Cập nhật giáo viên thất bại.", e);
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void delete(int id) {
		QLGiaoVien gv = getById(id);
		if (gv == null)
			return;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			deleteSubjectsByTeacherIDInternal(conn, gv.getTeacherID());

			String sql = "DELETE FROM QLGiaoVien WHERE ID=?";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, id);
				ps.executeUpdate();
			}
			conn.commit();
		} catch (SQLException e) {
			System.err.println("--- LỖI DELETE QLGiaoVien ---");
			System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
			System.err.println("Database Error Message: " + e.getMessage());
			e.printStackTrace();

			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw new RuntimeException("Xóa giáo viên thất bại do lỗi cơ sở dữ liệu.", e);
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<QLMonHoc> getSubjectsByTeacherID(String teacherID) {
		List<QLMonHoc> subjects = new ArrayList<>();
		String sql = "SELECT mh.SubjectID, mh.SubjectName FROM QLGVMonHoc gvmh JOIN QLMonHoc mh ON gvmh.SubjectID=mh.SubjectID WHERE gvmh.TeacherID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, teacherID);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					QLMonHoc mh = new QLMonHoc();
					mh.setSubjectID(rs.getInt("SubjectID"));
					mh.setSubjectName(rs.getString("SubjectName"));
					subjects.add(mh);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjects;
	}

	public void saveTeacherSubjects(String teacherID, List<Integer> subjectIDs) {
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			deleteSubjectsByTeacherIDInternal(conn, teacherID);

			if (subjectIDs != null && !subjectIDs.isEmpty()) {
				// Sửa: Gọi helper internal đã đổi tên để tránh lỗi đệ quy
				insertTeacherSubjectsInternal(conn, teacherID, subjectIDs);
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw new RuntimeException("Lỗi lưu môn học.", e);
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deleteSubjectsByTeacherID(String teacherID) {
		try (Connection conn = DBConnection.getConnection()) {
			deleteSubjectsByTeacherIDInternal(conn, teacherID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void updateStatus(String teacherID, boolean isActive) {
		String sql = "UPDATE QLGiaoVien SET IsActive=? WHERE TeacherID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setBoolean(1, isActive);
			ps.setString(2, teacherID);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void deleteTeacherSubjectsByIDs(Connection conn, String teacherID, List<Integer> subjectIDs)
			throws SQLException {
		if (subjectIDs == null || subjectIDs.isEmpty())
			return;
		String sql = "DELETE FROM QLGVMonHoc WHERE TeacherID=? AND SubjectID=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Integer id : subjectIDs) {
				ps.setString(1, teacherID);
				ps.setInt(2, id);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	

}