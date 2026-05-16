package model.DAO;

import model.bean.Role;
import java.util.List;

public interface UserRoleDAO {
    List<Role> getRolesByUserId(int userId);
    void assignRoles(int userId, List<Integer> roleIds);
    void removeRoles(int userId);
    
}
