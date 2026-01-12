package model.DAO.impl;

import config.DBConnection;
import model.DAO.MenuDAO;
import model.bean.Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAOImpl implements MenuDAO {

    public List<Menu> getUserMenus() {
        List<Menu> allMenus = new ArrayList<>();
        List<Menu> parentMenus = new ArrayList<>();
        String sql = "SELECT * FROM Menu ORDER BY Levels ASC, MenuOrder ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allMenus.add(mapResultSetToMenu(rs));
            }
            for (Menu m : allMenus) {
                if (m.getLevels() == 1) {
                    parentMenus.add(m);
                } else {
                    for (Menu p : parentMenus) {
                        if (m.getParentID() == p.getMenuID()) {
                            p.getSubMenus().add(m);
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parentMenus;
    }

    public Menu getById(int id) {
        String sql = "SELECT * FROM Menu WHERE MenuID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToMenu(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Menu m) {
        String sql = "INSERT INTO Menu (MenuName, IsActive, ControllerName, ActionName, Levels, ParentID, MenuOrder, Position, Icon, IDName, ItemTarget) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setMenuParameters(ps, m);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Menu m) {
        String sqlUpdateCurrent = "UPDATE Menu SET MenuName=?, IsActive=?, ControllerName=?, ActionName=?, Levels=?, ParentID=?, MenuOrder=?, Position=?, Icon=?, IDName=?, ItemTarget=? WHERE MenuID=?";
        String sqlDisableChildren = "UPDATE Menu SET IsActive = 0 WHERE ParentID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); 
            try {
               
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateCurrent)) {
                    setMenuParameters(ps, m);
                    ps.setInt(12, m.getMenuID());
                    ps.executeUpdate();
                }

               
                if (!m.isActive()) {
                    try (PreparedStatement psSub = conn.prepareStatement(sqlDisableChildren)) {
                        psSub.setInt(1, m.getMenuID());
                        psSub.executeUpdate();
                    }
                }
                conn.commit(); 
            } catch (SQLException e) {
                conn.rollback(); 
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Menu WHERE MenuID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Menu mapResultSetToMenu(ResultSet rs) throws SQLException {
        Menu m = new Menu();
        m.setMenuID(rs.getInt("MenuID"));
        m.setMenuName(rs.getString("MenuName"));
        m.setActive(rs.getBoolean("IsActive"));
        m.setControllerName(rs.getString("ControllerName"));
        m.setActionName(rs.getString("ActionName"));
        m.setLevels(rs.getInt("Levels"));
        m.setParentID(rs.getInt("ParentID"));
        m.setMenuOrder(rs.getInt("MenuOrder"));
        m.setPosition(rs.getInt("Position"));
        m.setIcon(rs.getString("Icon"));
        m.setIdName(rs.getString("IDName"));
        m.setItemTarget(rs.getString("ItemTarget"));
        m.setSubMenus(new ArrayList<>());
        return m;
    }

    private void setMenuParameters(PreparedStatement ps, Menu m) throws SQLException {
        ps.setString(1, m.getMenuName());
        ps.setBoolean(2, m.isActive());
        ps.setString(3, m.getControllerName());
        ps.setString(4, m.getActionName());
        ps.setInt(5, m.getLevels());
        ps.setInt(6, m.getParentID());
        ps.setInt(7, m.getMenuOrder());
        ps.setInt(8, m.getPosition());
        ps.setString(9, m.getIcon());
        ps.setString(10, m.getIdName());
        ps.setString(11, m.getItemTarget());
    }
}