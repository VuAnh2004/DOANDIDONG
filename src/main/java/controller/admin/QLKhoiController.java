package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.QLKhoiDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.QLKhoiDAOImpl;
import model.bean.AdminMenu;
import model.bean.QLKhoi;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
// THÊM CÁC IMPORT CẦN THIẾT CHO LOGIC BUILD TREE
import java.util.HashMap; 
import java.util.List;
import java.util.Map;

@WebServlet("/admin/QLKhoi/*")
public class QLKhoiController extends HttpServlet {

	private QLKhoiDAO dao = new QLKhoiDAOImpl();
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
			} 

			else if (controller != null && !controller.isEmpty() && action != null && !action.isEmpty()) {
				m.setItemTarget(contextPath + "/admin/" + controller + "/" + action);
			} 
			
			else {
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

		List<AdminMenu> flatMenus = adminMenuDAO.getActiveMenus();
	
		List<AdminMenu> treeMenus = buildMenuTree(flatMenus, request.getContextPath());
		request.setAttribute("menus", treeMenus);
		String action = request.getPathInfo();
		if (action == null || action.equals("/"))
			action = "/Index";

		switch (action) {
			case "/Index":
				List<QLKhoi> list = dao.getAll();
				request.setAttribute("list", list);

				request.setAttribute("contentPage", "/WEB-INF/admin/QLKhoi/Index.jsp");
				request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
				break;

			case "/Create":
				request.setAttribute("contentPage", "/WEB-INF/admin/QLKhoi/Create.jsp");
				request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
				break;

			case "/Edit":
				try {
					int id = Integer.parseInt(request.getParameter("id"));
					QLKhoi q = dao.getById(id);
					request.setAttribute("q", q);

					request.setAttribute("contentPage", "/WEB-INF/admin/QLKhoi/Edit.jsp");
					request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
				} catch (NumberFormatException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
				}
				break;

			case "/Delete":
				try {
					int id = Integer.parseInt(request.getParameter("id"));
					QLKhoi q = dao.getById(id);
					request.setAttribute("q", q);
					request.setAttribute("contentPage", "/WEB-INF/admin/QLKhoi/Delete.jsp");
					request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
				} catch (NumberFormatException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
				}
				break;


			default:
				response.sendRedirect(request.getContextPath() + "/admin/QLKhoi/Index");
				break;
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getPathInfo();

		if (action.equals("/Create")) {
			QLKhoi q = new QLKhoi();
			q.setGradeName(request.getParameter("GradeName"));
			q.setDescription(request.getParameter("Description"));
			q.setActive(request.getParameter("IsActive") != null);
			dao.insert(q);
			response.sendRedirect(request.getContextPath() + "/admin/QLKhoi/Index");

		} else if (action.equals("/Edit")) {
			QLKhoi q = new QLKhoi();
			q.setGradeLevelId(Integer.parseInt(request.getParameter("GradeLevelId")));
			q.setGradeName(request.getParameter("GradeName"));
			q.setDescription(request.getParameter("Description"));
			q.setActive(request.getParameter("IsActive") != null);
			dao.update(q);
			response.sendRedirect(request.getContextPath() + "/admin/QLKhoi/Index");

		} else if (action.equals("/ToggleStatus")) {
			int id = Integer.parseInt(request.getParameter("id"));
			QLKhoi khoi = dao.getById(id);
			if (khoi != null) {
				khoi.setActive(!khoi.isActive());
				dao.update(khoi);
			}
			response.sendRedirect(request.getContextPath() + "/admin/QLKhoi/Index");
		} else if (action.equals("/Delete")) {
			try {
				int id = Integer.parseInt(request.getParameter("GradeLevelId"));
				dao.delete(id);
				response.sendRedirect(request.getContextPath() + "/admin/QLKhoi/Index");
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
			}
		}

	}
}