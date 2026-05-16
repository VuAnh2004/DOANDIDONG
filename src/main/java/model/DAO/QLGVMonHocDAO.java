package model.DAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface QLGVMonHocDAO {
    List<Integer> getSubjectsByTeacher(String teacherID);   // Lấy danh sách ID môn đang dạy
    void insert(String teacherID, int subjectID);           // Thêm 1 môn
    void deleteByTeacher(String teacherID);                 // Xóa toàn bộ môn dạy
}
