package controller.admin;

import model.DAO.AccountDAO;
import model.DAO.AdminMenuDAO;
import model.DAO.RoleDAO;
import model.DAO.UserRoleDAO;
import model.DAO.impl.AccountDAOImpl;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.RoleDAOImpl;
import model.DAO.impl.UserRoleDAOImpl;
import model.bean.Account;
import model.bean.AdminMenu;
import model.bean.Role;
import Utilities.Functions;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin/Account/*")
public class AccountController extends HttpServlet {

    private AccountDAO accountDAO = new AccountDAOImpl();
    private RoleDAO roleDAO = new RoleDAOImpl();
    private UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
    private AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();

    private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
        Map<Integer, AdminMenu> map = new HashMap<>();
        for (AdminMenu m : flatMenus) {
            map.put((int) m.getAdminMenuID(), m);
            if (m.getIdName() == null || m.getIdName().isEmpty()) m.setIdName("menu-" + m.getAdminMenuID());
            if (m.getAdminMenuID() == 1 || ("Home".equalsIgnoreCase(m.getControllerName()) && "Index".equalsIgnoreCase(m.getActionName())))
                m.setItemTarget(contextPath + "/admin");
            else if (m.getControllerName() != null && !m.getControllerName().isEmpty()
                    && m.getActionName() != null && !m.getActionName().isEmpty())
                m.setItemTarget(contextPath + "/admin/" + m.getControllerName() + "/" + m.getActionName());
            else m.setItemTarget("#");
        }

        for (AdminMenu m : flatMenus) {
            if (m.getParentLevel() != 0) {
                AdminMenu parent = map.get(m.getParentLevel());
                if (parent != null) {
                    if (parent.getSubMenus() == null) parent.setSubMenus(new ArrayList<>());
                    parent.getSubMenus().add(m);
                }
            }
        }
        return flatMenus.stream().filter(m -> m.getParentLevel() == 0).collect(Collectors.toList());
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("menus", buildMenuTree(adminMenuDAO.getActiveMenus(), request.getContextPath()));
        String action = request.getPathInfo();
        if (action == null || action.equals("/")) action = "/Index";

        switch (action) {
            case "/Index":
                request.setAttribute("list", accountDAO.getAll());
                request.setAttribute("contentPage", "/WEB-INF/admin/Account/Index.jsp");
                break;

            case "/Create":
                request.setAttribute("roles", roleDAO.getAll());
                request.setAttribute("contentPage", "/WEB-INF/admin/Account/Create.jsp");
                break;
            case "/Edit":
                int editId = parseIntSafe(request.getParameter("id"), 0);
                if (editId > 0) {
                    Account acc = accountDAO.getById(editId);
                    if (acc != null) {
                        request.setAttribute("account", acc);
                        List<Role> allRoles = roleDAO.getAll();
                        request.setAttribute("roles", allRoles);
                        List<Role> userRoleList = userRoleDAO.getRolesByUserId(editId);
                        List<Integer> userRoleIds = userRoleList.stream()
                                .map(Role::getRoleID)
                                .collect(Collectors.toList());
                        request.setAttribute("userRoles", userRoleIds);
                    }
                }
                request.setAttribute("contentPage", "/WEB-INF/admin/Account/Edit.jsp");
                break;

            case "/Delete":
                int delId = parseIntSafe(request.getParameter("id"), 0);
                if (delId > 0) request.setAttribute("account", accountDAO.getById(delId));
                request.setAttribute("contentPage", "/WEB-INF/admin/Account/Delete.jsp");
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/admin/Account/Index");
                return;
        }

        request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/Account/Index");
            return;
        }

        switch (action) {
            case "/Create":
                String username = request.getParameter("UserName");
                String email = request.getParameter("Email");
                String password = request.getParameter("Password");
                int roleId = parseIntSafe(request.getParameter("RoleID"), 0);
                boolean isActive = request.getParameter("IsActive") != null;

                if (username != null && email != null && password != null) {
                    Account acc = new Account();
                    acc.setUserName(username);
                    acc.setEmail(email);
                    acc.setPassword(Functions.hashPassword(password));
                    acc.setActive(isActive);
                    accountDAO.insert(acc);

                    if (roleId > 0) userRoleDAO.assignRoles(acc.getUserID(), List.of(roleId));
                }
                break;

            case "/Edit":
                int id = parseIntSafe(request.getParameter("UserID"), 0);
                Account acc = accountDAO.getById(id);
                if (acc != null) {
                    acc.setUserName(request.getParameter("UserName"));
                    acc.setEmail(request.getParameter("Email"));

                    String pwd = request.getParameter("Password");
                    if (pwd != null && !pwd.isEmpty()) acc.setPassword(Functions.hashPassword(pwd));

                    acc.setActive(request.getParameter("IsActive") != null);
                    accountDAO.update(acc);

                    int newRoleId = parseIntSafe(request.getParameter("RoleID"), 0);
                    if (newRoleId > 0) userRoleDAO.assignRoles(id, List.of(newRoleId));
                }
                break;

            case "/Delete":
                int deleteId = parseIntSafe(request.getParameter("UserID"), 0);
                if (deleteId > 0) {
                    userRoleDAO.removeRoles(deleteId);
                    accountDAO.delete(deleteId);
                }
                break;

            case "/ToggleStatus":
                int toggleId = parseIntSafe(request.getParameter("id"), 0);
                Account a = accountDAO.getById(toggleId);
                if (a != null) {
                    a.setActive(!a.isActive());
                    accountDAO.update(a);
                }
                break;
        }

        response.sendRedirect(request.getContextPath() + "/admin/Account/Index");
    }

    private int parseIntSafe(String s, int defaultVal) {
        try {
            return (s != null && !s.isEmpty()) ? Integer.parseInt(s) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
