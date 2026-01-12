package model.DAO.impl;

import config.DBConnection;
import model.DAO.UserRoleDAO;
import model.bean.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRoleDAOImpl implements UserRoleDAO {

    public List<Role> getRolesByUserId(int userId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.RoleID, r.RoleName FROM tblRoles r " +
                     "JOIN tblUsersRoles ur ON r.RoleID = ur.RoleID " +
                     "WHERE ur.UserID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Role r = new Role();
                    r.setRoleID(rs.getInt("RoleID"));
                    r.setRoleName(rs.getString("RoleName"));
                    roles.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }


    public void assignRoles(int userId, List<Integer> roleIds) {
        removeRoles(userId); 
        String sql = "INSERT INTO tblUsersRoles(UserID, RoleID) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int roleId : roleIds) {
                ps.setInt(1, userId);
                ps.setInt(2, roleId);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeRoles(int userId) {
        String sql = "DELETE FROM tblUsersRoles WHERE UserID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
