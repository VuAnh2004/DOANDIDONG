package model.DAO.impl;

import config.DBConnection;
import model.DAO.AdminMenuDAO;
import model.bean.AdminMenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminMenuDAOImpl implements AdminMenuDAO {

    
    public List<AdminMenu> getActiveMenus() {
        List<AdminMenu> list = new ArrayList<>();
        String sql = "SELECT * FROM AdminMenu WHERE IsActive = 1 ORDER BY ItemOrder ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AdminMenu m = new AdminMenu();
                m.setAdminMenuID(rs.getLong("AdminMenuID"));
                m.setItemName(rs.getString("ItemName"));
                m.setItemLevel(rs.getInt("ItemLevel"));
                m.setParentLevel(rs.getInt("ParentLevel"));
                m.setItemOrder(rs.getInt("ItemOrder"));
                m.setIsActive(rs.getBoolean("IsActive"));
                m.setItemTarget(rs.getString("ItemTarget"));
                m.setAreaName(rs.getString("AreaName"));
                m.setControllerName(rs.getString("ControllerName"));
                m.setActionName(rs.getString("ActionName"));
                m.setIcon(rs.getString("Icon"));
                m.setIdName(rs.getString("IdName"));

                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
