package model.DAO;

import model.bean.Role;
import java.util.List;

public interface RoleDAO {
    List<Role> getAll();
    Role getById(int id);
}
