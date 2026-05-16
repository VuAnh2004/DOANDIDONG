package model.DAO;

import model.bean.QLLopHoc;
import java.util.List;

public interface QLLopHocDAO {
    List<QLLopHoc> getAll();
    QLLopHoc getById(int id);
    void insert(QLLopHoc lop);
    void update(QLLopHoc lop);
    void delete(int id);
}
