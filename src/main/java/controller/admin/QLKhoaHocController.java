package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.QLKhoaHocDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.QLKhoaHocDAOImpl;
import model.bean.AdminMenu;
import model.bean.QLKhoaHoc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/QLKhoaHoc/*") 
public class QLKhoaHocController extends HttpServlet { 
    private static final long serialVersionUID = 1L;
    
    private QLKhoaHocDAO dao = new QLKhoaHocDAOImpl();
    private AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();
    
    
    private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
    	
        List<AdminMenu> allMenus = new ArrayList<>(flatMenus); 
        Map<Integer, AdminMenu> menuMap = new HashMap<>(); 
        for (AdminMenu m : allMenus) { 
            menuMap.put((int)m.getAdminMenuID(), m); 
            if (m.getIdName() == null || m.getIdName().isEmpty()) { 
                m.setIdName("menu-" + m.getAdminMenuID());
            }

            String controller = m.getControllerName();
            String action = m.getActionName();
            
            if (m.getAdminMenuID() == 1 || ("Home".equalsIgnoreCase(controller) && "Index".equalsIgnoreCase(action))) {
                m.setItemTarget(contextPath + "/admin");
            } else if (controller != null && !controller.isEmpty() && action != null && !action.isEmpty()) {
                m.setItemTarget(contextPath + "/admin/" + controller + "/" + action);
            } else {
                m.setItemTarget("#");
            }
        }

        for (AdminMenu m : allMenus) {
            int parentId = m.getParentLevel();
            if (parentId != 0) {
                AdminMenu parent = menuMap.get(parentId);
                if (parent != null) {
                    if (parent.getSubMenus() == null)
                        parent.setSubMenus(new ArrayList<>());
                    parent.getSubMenus().add(m);
                }
            }
        }

        List<AdminMenu> rootMenus = new ArrayList<>();
        for (AdminMenu m : allMenus) {
            if (m.getParentLevel() == 0) {
                rootMenus.add(m);
            }
        }

        return rootMenus;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<AdminMenu> menus = adminMenuDAO.getActiveMenus();
        request.setAttribute("menus", buildMenuTree(menus, request.getContextPath()));

        String action = request.getPathInfo();
        if (action == null || action.equals("/")) action = "/Index";

        switch (action) {
            case "/Index":
                request.setAttribute("list", dao.getAll());
                request.setAttribute("contentPage", "/WEB-INF/admin/QLKhoaHoc/Index.jsp");
                request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
                break;

            case "/Create":
                request.setAttribute("contentPage", "/WEB-INF/admin/QLKhoaHoc/Create.jsp");
                request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
                break;

            case "/Edit":
                int editId = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("kh", dao.getById(editId));
                request.setAttribute("contentPage", "/WEB-INF/admin/QLKhoaHoc/Edit.jsp");
                request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
                break;

            case "/Delete":
                int delId = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("kh", dao.getById(delId));
                request.setAttribute("contentPage", "/WEB-INF/admin/QLKhoaHoc/Delete.jsp");
                request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/admin/QLKhoaHoc/Index");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();

        if ("/Create".equals(action)) {
            QLKhoaHoc kh = new QLKhoaHoc();
            kh.setStartYear(Integer.parseInt(request.getParameter("StartYear")));
            kh.setEndYear(Integer.parseInt(request.getParameter("EndYear")));
            kh.setActive(request.getParameter("IsActive") != null);
            kh.setCohort(kh.getStartYear() - 1964);
            dao.insert(kh);
            response.sendRedirect(request.getContextPath() + "/admin/QLKhoaHoc/Index");

        } else if ("/Edit".equals(action)) {
            QLKhoaHoc kh = new QLKhoaHoc();
            kh.setCourseID(Integer.parseInt(request.getParameter("CourseID")));
            kh.setStartYear(Integer.parseInt(request.getParameter("StartYear")));
            kh.setEndYear(Integer.parseInt(request.getParameter("EndYear")));
            kh.setActive(request.getParameter("IsActive") != null);
            kh.setCohort(60 + (kh.getStartYear() - 2024));
            dao.update(kh);
            response.sendRedirect(request.getContextPath() + "/admin/QLKhoaHoc/Index");

        } else if ("/Delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("CourseID"));
            dao.delete(id);
            response.sendRedirect(request.getContextPath() + "/admin/QLKhoaHoc/Index");

        } else if ("/ToggleStatus".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            QLKhoaHoc kh = dao.getById(id);
            if (kh != null) {
                kh.setActive(!kh.isActive());
                dao.update(kh);
            }
            response.sendRedirect(request.getContextPath() + "/admin/QLKhoaHoc/Index");
        }
    }
}
