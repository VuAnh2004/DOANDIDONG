package model.DAO;

import model.bean.QLDiem;
import java.util.List;

public interface QLDiemDAO {
    List<QLDiem> getAll();
    QLDiem getById(int gradeID);
    void insert(QLDiem diem);
    void update(QLDiem diem);
    void delete(int gradeID);
    List<QLDiem> getByTeacher(String teacherID);
}
