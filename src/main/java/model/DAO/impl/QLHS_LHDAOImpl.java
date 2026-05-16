package model.DAO.impl;

import config.DBConnection;
import model.DAO.*;
import model.bean.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLHS_LHDAOImpl implements QLHS_LHDAO {

	private QLHocSinhDAO hocSinhDAO = new QLHocSinhDAOImpl();
	private QLLopHocDAO lopHocDAO = new QLLopHocDAOImpl();
	private QLKhoaHocDAO khoaHocDAO = new QLKhoaHocDAOImpl();
	private QLHocKyDAO hocKyDAO = new QLHocKyDAOImpl();

	public List<QLHS_LH> getAll() {
		List<QLHS_LH> list = new ArrayList<>();
		String sql = "SELECT HocSinhLopHocID, StudentID, ClassID, IsActive, SemesterID, CourseID FROM QLHocSinhLopHoc ORDER BY HocSinhLopHocID";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				QLHS_LH hs_lh = new QLHS_LH();
				hs_lh.setHocSinhLopHocID(rs.getInt("HocSinhLopHocID"));
				hs_lh.setStudentID(rs.getString("StudentID"));
				hs_lh.setClassID(rs.getInt("ClassID"));
				hs_lh.setIsActive(rs.getBoolean("IsActive"));

				Object semObj = rs.getObject("SemesterID");
				if (semObj != null)
					hs_lh.setSemesterID((Integer) semObj);

				Object courseObj = rs.getObject("CourseID");
				if (courseObj != null)
					hs_lh.setCourseID((Integer) courseObj);

				if (hs_lh.getStudentID() != null)
					hs_lh.setHocsinh(hocSinhDAO.getByStudentID(hs_lh.getStudentID()));
				hs_lh.setLopHoc(lopHocDAO.getById(hs_lh.getClassID()));

				if (hs_lh.getSemesterID() != null)
					hs_lh.setHocKy(hocKyDAO.getById(hs_lh.getSemesterID()));
				if (hs_lh.getCourseID() != null)
					hs_lh.setKhoaHoc(khoaHocDAO.getById(hs_lh.getCourseID()));

				list.add(hs_lh);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}


	public QLHS_LH getById(int id) {
		String sql = "SELECT HocSinhLopHocID, StudentID, ClassID, IsActive, SemesterID, CourseID FROM QLHocSinhLopHoc WHERE HocSinhLopHocID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					QLHS_LH hs_lh = new QLHS_LH();
					hs_lh.setHocSinhLopHocID(rs.getInt("HocSinhLopHocID"));
					hs_lh.setStudentID(rs.getString("StudentID"));
					hs_lh.setClassID(rs.getInt("ClassID"));
					hs_lh.setIsActive(rs.getBoolean("IsActive"));

					Object semObj = rs.getObject("SemesterID");
					if (semObj != null)
						hs_lh.setSemesterID((Integer) semObj);

					Object courseObj = rs.getObject("CourseID");
					if (courseObj != null)
						hs_lh.setCourseID((Integer) courseObj);

					// Load quan hệ
					if (hs_lh.getStudentID() != null)
						hs_lh.setHocsinh(hocSinhDAO.getByStudentID(hs_lh.getStudentID()));
					hs_lh.setLopHoc(lopHocDAO.getById(hs_lh.getClassID()));

					if (hs_lh.getSemesterID() != null)
						hs_lh.setHocKy(hocKyDAO.getById(hs_lh.getSemesterID()));
					if (hs_lh.getCourseID() != null)
						hs_lh.setKhoaHoc(khoaHocDAO.getById(hs_lh.getCourseID()));

					return hs_lh;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void insert(QLHS_LH hs_lh) {
		String sql = "INSERT INTO QLHocSinhLopHoc (StudentID, ClassID, IsActive, SemesterID, CourseID) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, hs_lh.getStudentID());
			ps.setInt(2, hs_lh.getClassID());
			ps.setBoolean(3, hs_lh.getIsActive());

			if (hs_lh.getSemesterID() != null)
				ps.setInt(4, hs_lh.getSemesterID());
			else
				ps.setNull(4, Types.INTEGER);

			if (hs_lh.getCourseID() != null)
				ps.setInt(5, hs_lh.getCourseID());
			else
				ps.setNull(5, Types.INTEGER);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void update(QLHS_LH hs_lh) {
		String sql = "UPDATE QLHocSinhLopHoc SET StudentID=?, ClassID=?, IsActive=?, SemesterID=?, CourseID=? WHERE HocSinhLopHocID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, hs_lh.getStudentID());
			ps.setInt(2, hs_lh.getClassID());
			ps.setBoolean(3, hs_lh.getIsActive());

			if (hs_lh.getSemesterID() != null)
				ps.setInt(4, hs_lh.getSemesterID());
			else
				ps.setNull(4, Types.INTEGER);

			if (hs_lh.getCourseID() != null)
				ps.setInt(5, hs_lh.getCourseID());
			else
				ps.setNull(5, Types.INTEGER);

			ps.setInt(6, hs_lh.getHocSinhLopHocID());
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(int id) {
		String sql = "DELETE FROM QLHocSinhLopHoc WHERE HocSinhLopHocID=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int countUniqueStudentsInClass(int classId) {

		String sql = "SELECT COUNT(DISTINCT StudentID) FROM QLHocSinhLopHoc WHERE ClassID=? AND IsActive";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, classId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean existsInClassSemester(String studentID, int classID, int semesterID, int courseID) {
	    String sql = "SELECT COUNT(*) FROM QLHocSinhLopHoc WHERE StudentID=? AND ClassID=? AND SemesterID=? AND CourseID=?";
	    try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, studentID);
	        ps.setInt(2, classID);
	        ps.setInt(3, semesterID);
	        ps.setInt(4, courseID);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) { e.printStackTrace(); }
	    return false;
	}


}
