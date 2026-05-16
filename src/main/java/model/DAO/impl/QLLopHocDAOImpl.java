package model.DAO.impl;

import config.DBConnection;
import model.DAO.QLLopHocDAO;
import model.DAO.QLKhoiDAO;
import model.DAO.QLKhoaHocDAO;
import model.bean.QLLopHoc;
import model.bean.QLKhoi;
import model.bean.QLKhoaHoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLLopHocDAOImpl implements QLLopHocDAO {

    private QLKhoiDAO qlKhoiDAO = new QLKhoiDAOImpl();   
    private QLKhoaHocDAO qlKhoaHocDAO = new QLKhoaHocDAOImpl(); 


    public List<QLLopHoc> getAll() {
        List<QLLopHoc> list = new ArrayList<>();
        String sql = "SELECT ClassID, ClassName, GradeLevelId, CourseID, MaxStudents, CurrentStudents, SchoolYear, IsActive " +
                     "FROM QLLopHoc ORDER BY ClassID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                QLLopHoc q = new QLLopHoc();
                q.setClassID(rs.getInt("ClassID"));
                q.setClassName(rs.getString("ClassName"));
                q.setGradeLevelId(rs.getInt("GradeLevelId"));
                Object courseObj = rs.getObject("CourseID");
                if(courseObj != null) q.setCourseID((Integer) courseObj);

                q.setMaxStudents(rs.getInt("MaxStudents"));

                Object currentObj = rs.getObject("CurrentStudents");
                if(currentObj != null) q.setCurrentStudents((Integer) currentObj);

                q.setSchoolYear(rs.getString("SchoolYear"));
                q.setActive(rs.getBoolean("IsActive"));

                QLKhoi khoi = qlKhoiDAO.getById(q.getGradeLevelId());
                q.setKhois(khoi);

     
                if(q.getCourseID() != null) {
                    QLKhoaHoc khoaHoc = qlKhoaHocDAO.getById(q.getCourseID());
                    q.setKhoaHoc(khoaHoc);
                }

                list.add(q);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public QLLopHoc getById(int id) {
        String sql = "SELECT ClassID, ClassName, GradeLevelId, CourseID, MaxStudents, CurrentStudents, SchoolYear, IsActive " +
                     "FROM QLLopHoc WHERE ClassID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    QLLopHoc q = new QLLopHoc();
                    q.setClassID(rs.getInt("ClassID"));
                    q.setClassName(rs.getString("ClassName"));
                    q.setGradeLevelId(rs.getInt("GradeLevelId"));

                    Object courseObj = rs.getObject("CourseID");
                    if(courseObj != null) q.setCourseID((Integer) courseObj);

                    q.setMaxStudents(rs.getInt("MaxStudents"));

                    Object currentObj = rs.getObject("CurrentStudents");
                    if(currentObj != null) q.setCurrentStudents((Integer) currentObj);

                    q.setSchoolYear(rs.getString("SchoolYear"));
                    q.setActive(rs.getBoolean("IsActive"));

                    // Load khối
                    QLKhoi khoi = qlKhoiDAO.getById(q.getGradeLevelId());
                    q.setKhois(khoi);

                    // Load khóa học
                    if(q.getCourseID() != null) {
                        QLKhoaHoc khoaHoc = qlKhoaHocDAO.getById(q.getCourseID());
                        q.setKhoaHoc(khoaHoc);
                    }

                    return q;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(QLLopHoc q) {
        String sql = "INSERT INTO QLLopHoc (ClassName, GradeLevelId, CourseID, MaxStudents, CurrentStudents, SchoolYear, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getClassName());
            ps.setInt(2, q.getGradeLevelId());

            if (q.getCourseID() != null)
                ps.setInt(3, q.getCourseID());
            else
                ps.setNull(3, Types.INTEGER);

            ps.setInt(4, q.getMaxStudents());

            if (q.getCurrentStudents() != null)
                ps.setInt(5, q.getCurrentStudents());
            else
                ps.setNull(5, Types.INTEGER);

            ps.setString(6, q.getSchoolYear());
            ps.setBoolean(7, q.isActive());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(QLLopHoc q) {
        String sql = "UPDATE QLLopHoc SET ClassName=?, GradeLevelId=?, CourseID=?, MaxStudents=?, CurrentStudents=?, SchoolYear=?, IsActive=? " +
                     "WHERE ClassID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getClassName());
            ps.setInt(2, q.getGradeLevelId());

            if (q.getCourseID() != null)
                ps.setInt(3, q.getCourseID());
            else
                ps.setNull(3, Types.INTEGER);

            ps.setInt(4, q.getMaxStudents());

            if (q.getCurrentStudents() != null)
                ps.setInt(5, q.getCurrentStudents());
            else
                ps.setNull(5, Types.INTEGER);

            ps.setString(6, q.getSchoolYear());
            ps.setBoolean(7, q.isActive());
            ps.setInt(8, q.getClassID());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM QLLopHoc WHERE ClassID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateCurrentStudents(int classID, int newCount) {
        String sql = "UPDATE QLLopHoc SET CurrentStudents=? WHERE ClassID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newCount);
            ps.setInt(2, classID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy số học sinh đang active trong lớp (dùng để tính CurrentStudents)
    public int countActiveStudents(int classID) {
        String sql = "SELECT COUNT(DISTINCT StudentID) FROM QLHocSinhLopHoc WHERE ClassID=? AND IsActive=1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    
 // Lấy danh sách lớp + khóa duy nhất
    public List<QLLopHoc> getClassUnique() {
        List<QLLopHoc> classList = new ArrayList<>();

        String sql = """
            SELECT DISTINCT l.ClassID, l.ClassName, k.Cohort
            FROM QLLopHoc l
            LEFT JOIN QLKhoaHoc k ON l.CourseID = k.CourseID
            WHERE l.IsActive = 1
            ORDER BY l.ClassName
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                QLLopHoc cls = new QLLopHoc();
                cls.setClassID(rs.getInt("ClassID"));
                cls.setClassName(rs.getString("ClassName"));

                QLKhoaHoc kh = new QLKhoaHoc();
                kh.setCohort(rs.getInt("Cohort"));
                cls.setKhoaHoc(kh);

                classList.add(cls);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classList;
    }

}
