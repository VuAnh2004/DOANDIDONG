package model.DAO;

import model.bean.QLHS_LH;
import java.util.List;

public interface QLHS_LHDAO {
    List<QLHS_LH> getAll();
    QLHS_LH getById(int id);
    void insert(QLHS_LH hs_lh);
    void update(QLHS_LH hs_lh);
    void delete(int id);

    // THÊM ĐỂ CHECK TỒN TẠI
    boolean existsInClassSemester(String studentID, int classID, int semesterID, int courseID);
}
