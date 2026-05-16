package model.DAO;

import model.bean.QLMonHoc;
import java.util.List;

public interface QLMonHocDAO {
    List<QLMonHoc> getAll();
    QLMonHoc getById(int id);
    void insert(QLMonHoc mh);
    void update(QLMonHoc mh);
    void delete(int id);
}