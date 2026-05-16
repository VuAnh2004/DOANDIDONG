package model.DAO;

import java.util.List;
import model.bean.Menu;

public interface MenuDAO {
    List<Menu> getUserMenus();
    Menu getById(int id);
    void insert(Menu q);
    void update(Menu q);
    void delete(int id);
}
