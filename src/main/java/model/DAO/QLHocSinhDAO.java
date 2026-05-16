package model.DAO;

import model.bean.QLHocSinh;
import java.util.List;

public interface QLHocSinhDAO {
    List<QLHocSinh> getAll();
    QLHocSinh getById(int id);
    QLHocSinh getByStudentID(String studentID); // thêm phương thức kiểm tra trùng
    void insert(QLHocSinh s);
    void update(QLHocSinh s);
    void delete(int id);
    public List<QLHocSinh> getByClass(Integer classId, Integer cohort);

}
