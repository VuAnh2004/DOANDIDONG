package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.QLMonHocDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.QLMonHocDAOImpl;
import model.bean.AdminMenu;
import model.bean.QLMonHoc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/QLMonHoc/*")
public class QLMonHocController extends HttpServlet {

    private QLMonHocDAO dao = new QLMonHocDAOImpl();
    private AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();

    
    private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
        List<AdminMenu> allMenus = new ArrayList<>(flatMenus);
        Map<Integer, AdminMenu> menuMap = new HashMap<>();

        for (AdminMenu m : allMenus) {
            menuMap.put((int) m.getAdminMenuID(), m);
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
        if (action == null || action.equals("/"))
            action = "/Index";

        switch (action) {
            case "/Index":
                request.setAttribute("list", dao.getAll());
                request.setAttribute("contentPage", "/WEB-INF/admin/QLMonHoc/Index.jsp");
                request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
                break;

            case "/Create":
                request.setAttribute("contentPage", "/WEB-INF/admin/QLMonHoc/Create.jsp");
                request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
                break;

            case "/Edit":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("q", dao.getById(id));
                    request.setAttribute("contentPage", "/WEB-INF/admin/QLMonHoc/Edit.jsp");
                    request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Index");
                }
                break;

            case "/Delete":
                 try {
                    int idDel = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("q", dao.getById(idDel));
                    request.setAttribute("contentPage", "/WEB-INF/admin/QLMonHoc/Delete.jsp");
                    request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Index");
                }
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Index");
        }
    }

   
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8"); 
        String action = request.getPathInfo();

        if (action.equals("/Create")) {
            QLMonHoc mh = new QLMonHoc();
            mh.setSubjectName(request.getParameter("SubjectName"));

            try {
                mh.setNumberOfLesson(Integer.parseInt(request.getParameter("NumberOfLesson")));
            } catch (NumberFormatException e) {
                System.err.println("Lỗi chuyển đổi số tiết học: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Create?error=number");
                return;
            }

            mh.setSemester(request.getParameter("Semester"));
            mh.setActive(request.getParameter("IsActive") != null);

            dao.insert(mh);
            response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Index");

        } else if (action.equals("/Edit")) {
             QLMonHoc mh = new QLMonHoc();

             try {
                 mh.setSubjectID(Integer.parseInt(request.getParameter("SubjectID")));
                 mh.setNumberOfLesson(Integer.parseInt(request.getParameter("NumberOfLesson")));
             } catch (NumberFormatException e) {
                 System.err.println("Lỗi chuyển đổi ID hoặc số tiết học: " + e.getMessage());
                 response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Edit?id=" + request.getParameter("SubjectID") + "&error=number");
                 return;
             }

             mh.setSubjectName(request.getParameter("SubjectName"));
             mh.setSemester(request.getParameter("Semester"));
             mh.setActive(request.getParameter("IsActive") != null);

             dao.update(mh);
             response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Index");

        } else if (action.equals("/Delete")) {
            try {
                int id = Integer.parseInt(request.getParameter("SubjectID"));
                dao.delete(id);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi chuyển đổi ID xóa: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Index");

        } else if (action.equals("/ToggleStatus")) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                QLMonHoc mh = dao.getById(id);
                if (mh != null) {
                    mh.setActive(!mh.isActive());
                    dao.update(mh);
                }
            } catch (NumberFormatException e) {
                 System.err.println("Lỗi chuyển đổi ID Toggle: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Index");
        }
    }
}