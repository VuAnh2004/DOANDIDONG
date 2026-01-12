package model.DAO.impl;

import config.DBConnection;
import model.DAO.AccountDAO;
import model.bean.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {


    
    public List<Account> getAll() {
        List<Account> list = new ArrayList<>();
        String sql = """
            SELECT a.*, r.RoleID, r.RoleName
            FROM Account a
            LEFT JOIN tblUsersRoles ur ON a.UserID = ur.UserID
            LEFT JOIN tblRoles r ON ur.RoleID = r.RoleID
            ORDER BY a.UserID
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Account a = new Account();
                a.setUserID(rs.getInt("UserID"));
                a.setUserName(rs.getString("UserName"));
                a.setEmail(rs.getString("Email"));
                a.setPassword(rs.getString("Password"));
                a.setActive(rs.getBoolean("IsActive"));
                a.setFirstLogin(rs.getBoolean("IsFirstLogin"));
                a.setDate(rs.getTimestamp("Date"));
                a.setCount(rs.getInt("Count"));

                // Thêm 2 field mới
                a.setRoleID(rs.getInt("RoleID"));
                a.setRoleName(rs.getString("RoleName"));

                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    
    public Account getById(int id) {
        String sql = "SELECT * FROM Account WHERE UserID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account a = new Account();
                    a.setUserID(rs.getInt("UserID"));
                    a.setUserName(rs.getString("UserName"));
                    a.setEmail(rs.getString("Email"));
                    a.setPassword(rs.getString("Password"));
                    a.setActive(rs.getBoolean("IsActive"));
                    a.setFirstLogin(rs.getBoolean("IsFirstLogin"));
                    a.setDate(rs.getTimestamp("Date"));
                    a.setCount(rs.getInt("Count"));
                    return a;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

   
    public void insert(Account account) {
        String sql = "INSERT INTO Account(UserName, Email, Password, IsActive) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUserName());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPassword());
            ps.setBoolean(4, account.isActive());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setUserID(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void update(Account account) {
        String sql = "UPDATE Account SET UserName=?, Email=?, Password=?, IsActive=? WHERE UserID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getUserName());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPassword());
            ps.setBoolean(4, account.isActive());
            ps.setInt(5, account.getUserID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void delete(int id) {
        String sql = "DELETE FROM Account WHERE UserID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void toggleActive(int id, boolean status) {
        String sql = "UPDATE Account SET IsActive=? WHERE UserID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
}
