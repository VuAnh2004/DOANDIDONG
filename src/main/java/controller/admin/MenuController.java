package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.MenuDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.MenuDAOImpl;
import model.bean.AdminMenu;
import model.bean.Menu;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/Menu/*")
public class MenuController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MenuDAO dao = new MenuDAOImpl();
    private AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<AdminMenu> menus = adminMenuDAO.getActiveMenus();
        request.setAttribute("menus", buildMenuTree(menus, request.getContextPath()));

        String action = request.getPathInfo();
        if (action == null || action.equals("/")) action = "/Index";

        switch (action) {
            case "/Index" -> {
                request.setAttribute("listMenu", dao.getUserMenus());
                request.setAttribute("contentPage", "/WEB-INF/admin/Menu/Index.jsp");
            }
            case "/Create" -> {
                request.setAttribute("listMenu", dao.getUserMenus());
                request.setAttribute("contentPage", "/WEB-INF/admin/Menu/Create.jsp");
            }
            case "/Edit" -> {
                int id = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("menuItem", dao.getById(id));
                request.setAttribute("listMenu", dao.getUserMenus());
                request.setAttribute("contentPage", "/WEB-INF/admin/Menu/Edit.jsp");
            }
            case "/Delete" -> {
                int id = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("menuItem", dao.getById(id));
                request.setAttribute("contentPage", "/WEB-INF/admin/Menu/Delete.jsp");
            }
            default -> {
                response.sendRedirect(request.getContextPath() + "/admin/Menu/Index");
                return;
            }
        }
        request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();

        if ("/Create".equals(action)) {
            dao.insert(mapRequestToBean(request));
        } else if ("/Edit".equals(action)) {
            Menu m = mapRequestToBean(request);
            m.setMenuID(Integer.parseInt(request.getParameter("MenuID")));
            dao.update(m);
        } else if ("/Delete".equals(action)) {
            dao.delete(Integer.parseInt(request.getParameter("MenuID")));
        } else if ("/ToggleStatus".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Menu m = dao.getById(id);
            if (m != null) {
                m.setActive(!m.isActive());
                dao.update(m); // Logic Cascade xóa con nằm ở đây
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/Menu/Index");
    }

    private Menu mapRequestToBean(HttpServletRequest request) {
        Menu m = new Menu();
        m.setMenuName(request.getParameter("MenuName"));
        m.setControllerName(request.getParameter("ControllerName"));
        m.setActionName(request.getParameter("ActionName"));
        m.setLevels(Integer.parseInt(request.getParameter("Levels") != null ? request.getParameter("Levels") : "1"));
        m.setParentID(Integer.parseInt(request.getParameter("ParentID") != null ? request.getParameter("ParentID") : "0"));
        m.setMenuOrder(Integer.parseInt(request.getParameter("MenuOrder") != null ? request.getParameter("MenuOrder") : "1"));
        m.setPosition(Integer.parseInt(request.getParameter("Position") != null ? request.getParameter("Position") : "1"));
        m.setIcon(request.getParameter("Icon"));
        m.setIdName(request.getParameter("IDName"));
        m.setItemTarget(request.getParameter("ItemTarget"));
        m.setActive(request.getParameter("IsActive") != null);
        return m;
    }

    private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
        Map<Integer, AdminMenu> menuMap = new HashMap<>();
        List<AdminMenu> rootMenus = new ArrayList<>();
        for (AdminMenu m : flatMenus) {
            menuMap.put((int) m.getAdminMenuID(), m);
            if (m.getIdName() == null || m.getIdName().isEmpty()) m.setIdName("menu-" + m.getAdminMenuID());
            if (m.getAdminMenuID() == 1 || ("Home".equalsIgnoreCase(m.getControllerName()) && "Index".equalsIgnoreCase(m.getActionName()))) {
                m.setItemTarget(contextPath + "/admin");
            } else if (m.getControllerName() != null && !m.getControllerName().isEmpty()) {
                m.setItemTarget(contextPath + "/admin/" + m.getControllerName() + "/" + (m.getActionName() != null ? m.getActionName() : "Index"));
            } else {
                m.setItemTarget("#");
            }
        }
        for (AdminMenu m : flatMenus) {
            if (m.getParentLevel() == 0) rootMenus.add(m);
            else {
                AdminMenu parent = menuMap.get(m.getParentLevel());
                if (parent != null) {
                    if (parent.getSubMenus() == null) parent.setSubMenus(new ArrayList<>());
                    parent.getSubMenus().add(m);
                }
            }
        }
        return rootMenus;
    }
}