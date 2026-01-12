package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.QLHocKyDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.QLHocKyDAOImpl;
import model.bean.AdminMenu;
import model.bean.QLHocKy;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
// THÊM CÁC IMPORT CẦN THIẾT CHO LOGIC BUILD TREE
import java.util.HashMap; 
import java.util.List;
import java.util.Map;

@WebServlet("/admin/QLHocKy/*")
public class QLHocKyController extends HttpServlet {

	private QLHocKyDAO dao = new QLHocKyDAOImpl();
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

	@Override
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
				request.setAttribute("contentPage", "/WEB-INF/admin/QLHocKy/Index.jsp");
				request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
				break;

			case "/Create":
				request.setAttribute("contentPage", "/WEB-INF/admin/QLHocKy/Create.jsp");
				request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
				break;

			case "/Edit":
				int id = Integer.parseInt(request.getParameter("id"));
				request.setAttribute("q", dao.getById(id));
				request.setAttribute("contentPage", "/WEB-INF/admin/QLHocKy/Edit.jsp");
				request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
				break;

			case "/Delete":
				int idDel = Integer.parseInt(request.getParameter("id"));
				request.setAttribute("q", dao.getById(idDel));
				request.setAttribute("contentPage", "/WEB-INF/admin/QLHocKy/Delete.jsp");
				request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
				break;

			default:
				response.sendRedirect(request.getContextPath() + "/admin/QLHocKy/Index");
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getPathInfo();

		if (action.equals("/Create")) {
			QLHocKy hk = new QLHocKy();
			hk.setSemesterName(request.getParameter("SemesterName"));
			hk.setSemesterCode(request.getParameter("SemesterCode"));
			hk.setActive(request.getParameter("IsActive") != null);
			dao.insert(hk);
			response.sendRedirect(request.getContextPath() + "/admin/QLHocKy/Index");
		} else if (action.equals("/Edit")) {
			QLHocKy hk = new QLHocKy();
			hk.setSemesterId(Integer.parseInt(request.getParameter("SemesterID")));
			hk.setSemesterName(request.getParameter("SemesterName"));
			hk.setSemesterCode(request.getParameter("SemesterCode"));
			hk.setActive(request.getParameter("IsActive") != null);
			dao.update(hk);
			response.sendRedirect(request.getContextPath() + "/admin/QLHocKy/Index");
		} else if (action.equals("/Delete")) {
			int id = Integer.parseInt(request.getParameter("SemesterID"));
			dao.delete(id);
			response.sendRedirect(request.getContextPath() + "/admin/QLHocKy/Index");
		} else if (action.equals("/ToggleStatus")) {
			int id = Integer.parseInt(request.getParameter("id"));
			QLHocKy hk = dao.getById(id);
			if (hk != null) {
				hk.setActive(!hk.isActive());
				dao.update(hk);
			}
			response.sendRedirect(request.getContextPath() + "/admin/QLHocKy/Index");
		}


	}
}