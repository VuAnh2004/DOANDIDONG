package model.DAO;

import java.util.List;
import model.bean.AdminMenu;

public interface AdminMenuDAO {
    List<AdminMenu> getActiveMenus();
}
