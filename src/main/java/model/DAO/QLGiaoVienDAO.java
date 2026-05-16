package model.DAO;

import model.bean.QLGiaoVien;
import model.bean.QLMonHoc;

import java.util.List;

public interface QLGiaoVienDAO {
    List<QLGiaoVien> getAll();
    QLGiaoVien getById(int id);
    void insert(QLGiaoVien gv);
    void update(QLGiaoVien gv);
    void delete(int id);
    List<QLMonHoc> getSubjectsByTeacherID(String teacherID);

}

