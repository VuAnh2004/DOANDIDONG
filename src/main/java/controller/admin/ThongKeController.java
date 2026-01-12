package controller.admin;

import model.DAO.*;
import model.DAO.impl.*;
import model.bean.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/admin/") 
public class ThongKeController extends HttpServlet {

    private final QLHocSinhDAO hsDAO = new QLHocSinhDAOImpl();
    private final QLGiaoVienDAO gvDAO = new QLGiaoVienDAOImpl();
    private final QLLopHocDAO lhDAO = new QLLopHocDAOImpl();
    private final AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 1. Thống kê số lượng
        req.setAttribute("totalHS", hsDAO.getAll().size());
        req.setAttribute("totalGV", gvDAO.getAll().size());
        req.setAttribute("totalLop", lhDAO.getAll().size());

        // 2. Lấy danh sách học sinh (Recent Students)
        List<QLHocSinh> listHS = hsDAO.getAll();
        List<QLHocSinh> recentStudents = (listHS.size() > 5) ? listHS.subList(listHS.size() - 5, listHS.size()) : listHS;
        req.setAttribute("recentStudents", recentStudents);

        // 3. Đổ dữ liệu Menu Sidebar (QUAN TRỌNG: Phải giống AdminController)
        List<AdminMenu> flatMenus = adminMenuDAO.getActiveMenus();
        req.setAttribute("menus", buildMenuTree(flatMenus, req.getContextPath()));

        // Gửi sang index.jsp
        req.getRequestDispatcher("/admin/index.jsp").forward(req, resp);
    }

    private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
        Map<Integer, AdminMenu> menuMap = new HashMap<>();
        List<AdminMenu> rootMenus = new ArrayList<>();

        // Khởi tạo và tạo link
        for (AdminMenu m : flatMenus) {
            m.setSubMenus(new ArrayList<>()); // Dùng tên này nếu Sidebar dùng c:forEach items="${menu.subMenus}"
            menuMap.put((int) m.getAdminMenuID(), m);
            
            // Logic tạo Link Target
            if (m.getAdminMenuID() == 1) {
                m.setItemTarget(contextPath + "/admin/ThongKe");
            } else if (m.getControllerName() != null && m.getActionName() != null) {
                m.setItemTarget(contextPath + "/admin/" + m.getControllerName() + "/" + m.getActionName());
            } else {
                m.setItemTarget("#");
            }
        }

        // Xây dựng cây
        for (AdminMenu m : flatMenus) {
            if (m.getParentLevel() == 0) {
                rootMenus.add(m);
            } else {
                AdminMenu parent = menuMap.get(m.getParentLevel());
                if (parent != null) parent.getSubMenus().add(m);
            }
        }
        return rootMenus;
    }
}