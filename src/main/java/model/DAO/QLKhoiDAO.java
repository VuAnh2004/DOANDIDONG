package model.DAO;

import model.bean.QLKhoi;
import java.util.List;

public interface QLKhoiDAO {
    List<QLKhoi> getAll();
    QLKhoi getById(int id);
    void insert(QLKhoi q);
    void update(QLKhoi q);
    void delete(int id);
}
