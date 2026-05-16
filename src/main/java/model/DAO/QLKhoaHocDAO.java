package model.DAO;

import model.bean.QLKhoaHoc;
import java.util.List;

public interface QLKhoaHocDAO {
    List<QLKhoaHoc> getAll();
    QLKhoaHoc getById(int id);
    void insert(QLKhoaHoc q);
    void update(QLKhoaHoc q);
    void delete(int id);
}
