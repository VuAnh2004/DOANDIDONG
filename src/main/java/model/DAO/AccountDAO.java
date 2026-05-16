package model.DAO;

import model.bean.Account;
import java.util.List;

public interface AccountDAO {
    List<Account> getAll();
    Account getById(int id);
    void insert(Account account);
    void update(Account account);
    void delete(int id);
    void toggleActive(int id, boolean status);
}
