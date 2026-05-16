package model.DAO;

import model.bean.QLHocKy;
import java.util.List;

public interface QLHocKyDAO {
    List<QLHocKy> getAll();
    QLHocKy getById(int id);
    void insert(QLHocKy hk);
    void update(QLHocKy hk);
    void delete(int id);
}
