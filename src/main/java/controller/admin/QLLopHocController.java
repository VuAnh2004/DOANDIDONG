package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.QLLopHocDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.QLKhoaHocDAOImpl;
import model.DAO.impl.QLKhoiDAOImpl;
import model.DAO.impl.QLLopHocDAOImpl;
import model.bean.AdminMenu;
import model.bean.QLKhoaHoc;
import model.bean.QLKhoi;
import model.bean.QLLopHoc;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin/QLLopHoc/*")
public class QLLopHocController extends HttpServlet {

	private QLLopHocDAO dao = new QLLopHocDAOImpl();
	private AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();

	private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
		Map<Integer, AdminMenu> menuMap = new HashMap<>();
		for (AdminMenu m : flatMenus) {
			menuMap.put((int) m.getAdminMenuID(), m);
			if (m.getIdName() == null || m.getIdName().isEmpty())
				m.setIdName("menu-" + m.getAdminMenuID());

			String controller = m.getControllerName();
			String action = m.getActionName();
			if (m.getAdminMenuID() == 1 || ("Home".equalsIgnoreCase(controller) && "Index".equalsIgnoreCase(action))) {
				m.setItemTarget(contextPath + "/admin");
			} else if (controller != null && !controller.isEmpty() && action != null && !action.isEmpty()) {
				m.setItemTarget(contextPath + "/admin/" + controller + "/" + action);
			} else
				m.setItemTarget("#");
		}

		for (AdminMenu m : flatMenus) {
			if (m.getParentLevel() != 0) {
				AdminMenu parent = menuMap.get(m.getParentLevel());
				if (parent != null) {
					if (parent.getSubMenus() == null)
						parent.setSubMenus(new ArrayList<>());
					parent.getSubMenus().add(m);
				}
			}
		}

		List<AdminMenu> rootMenus = new ArrayList<>();
		for (AdminMenu m : flatMenus) {
			if (m.getParentLevel() == 0)
				rootMenus.add(m);
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
			request.setAttribute("list", dao.getAll());
			request.setAttribute("contentPage", "/WEB-INF/admin/QLLopHoc/Index.jsp");
			break;


		case "/Create":
			QLKhoaHocDAOImpl khoaHocDAO = new QLKhoaHocDAOImpl();
			List<QLKhoaHoc> khoaHocList = khoaHocDAO.getAll(); 
			request.setAttribute("KhoaHocList", khoaHocList);

			request.setAttribute("ClassName",
					request.getParameter("ClassName") != null ? request.getParameter("ClassName") : "");
			request.setAttribute("MaxStudents",
					request.getParameter("MaxStudents") != null ? request.getParameter("MaxStudents") : "");
			request.setAttribute("CourseID",
					request.getParameter("CourseID") != null ? request.getParameter("CourseID") : "");
			request.setAttribute("SchoolYear",
					request.getParameter("SchoolYear") != null ? request.getParameter("SchoolYear") : "");
			request.setAttribute("IsActive", request.getParameter("IsActive") != null);

			request.setAttribute("contentPage", "/WEB-INF/admin/QLLopHoc/Create.jsp");
			break;

		case "/Edit":
		    try {
		        String idStr = request.getParameter("id");
		        if (idStr != null) {
		            int id = Integer.parseInt(idStr);
		            QLLopHoc lop = dao.getById(id);
		            if (lop != null) {
		                request.setAttribute("lophoc", lop);

		                QLKhoiDAOImpl khoiDAO = new QLKhoiDAOImpl();
		                List<QLKhoi> khoiList = khoiDAO.getAll()
		                        .stream()
		                        .sorted(Comparator.comparingInt(QLKhoi::getGradeLevelId))
		                        .collect(Collectors.toList());
		                request.setAttribute("khoiList", khoiList);

		                QLKhoaHocDAOImpl khoaHocDAO1 = new QLKhoaHocDAOImpl();
		                request.setAttribute("KhoaHocList", khoaHocDAO1.getAll());
		            }
		        }
		        request.setAttribute("contentPage", "/WEB-INF/admin/QLLopHoc/Edit.jsp");
		    } catch (NumberFormatException e) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
		        return;
		    }
		    break;


		case "/Delete":
			try {
				String idStr = request.getParameter("id");
				if (idStr != null) {
					int id = Integer.parseInt(idStr);
					QLLopHoc l = dao.getById(id);
					if (l != null)
						request.setAttribute("lophoc", l);
				}
				request.setAttribute("contentPage", "/WEB-INF/admin/QLLopHoc/Delete.jsp");
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
				return;
			}
			break;

		default:
			response.sendRedirect(request.getContextPath() + "/admin/QLLopHoc/Index");
			return;
		}

		request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getPathInfo();

		if (action == null) {
			response.sendRedirect(request.getContextPath() + "/admin/QLLopHoc/Index");
			return;
		}

		switch (action) {
		case "/Create":
			try {
				String className = request.getParameter("ClassName");
				Integer courseId = parseIntegerNullable(request.getParameter("CourseID"));
				int maxStudents = parseIntSafe(request.getParameter("MaxStudents"), 0);
				boolean isActive = request.getParameter("IsActive") != null;

				if (courseId != null) {
					QLKhoaHocDAOImpl khoaHocDAO = new QLKhoaHocDAOImpl();
					QLKhoaHoc khoaHoc = khoaHocDAO.getById(courseId);

					if (khoaHoc != null && khoaHoc.getStartYear() != null && khoaHoc.getEndYear() != null) {
						QLKhoiDAOImpl khoiDAO = new QLKhoiDAOImpl();
						List<QLKhoi> khoiList = khoiDAO.getAll().stream()
								.sorted(Comparator.comparingInt(QLKhoi::getGradeLevelId)).collect(Collectors.toList());

						int totalYears = Math.min(khoiList.size(), khoaHoc.getEndYear() - khoaHoc.getStartYear() + 1);

						List<QLLopHoc> existingLops = dao.getAll();

						for (int i = 0; i < totalYears; i++) {
							QLLopHoc lopMoi = new QLLopHoc();
							lopMoi.setClassName(className);
							lopMoi.setCourseID(courseId);
							lopMoi.setGradeLevelId(khoiList.get(i).getGradeLevelId());

							// Tính SchoolYear
							lopMoi.setSchoolYear(computeSchoolYear(khoaHoc, khoiList.get(i), i));

							lopMoi.setMaxStudents(maxStudents);
							lopMoi.setCurrentStudents(0);
							lopMoi.setActive(isActive);

							boolean exists = existingLops.stream()
									.anyMatch(x -> x.getClassName().equals(lopMoi.getClassName())
											&& x.getCourseID().equals(lopMoi.getCourseID())
											&& x.getSchoolYear().equals(lopMoi.getSchoolYear()));

							if (!exists) {
								dao.insert(lopMoi);
								existingLops.add(lopMoi);
							}
						}
					}
				}

				response.sendRedirect(request.getContextPath() + "/admin/QLLopHoc/Index");
				return;
			} catch (Exception ex) {
				ex.printStackTrace();
				request.setAttribute("errorMessage", "Lỗi thêm lớp: " + ex.getMessage());

				QLKhoaHocDAOImpl khoaHocDAO = new QLKhoaHocDAOImpl();
				request.setAttribute("KhoaHocList", khoaHocDAO.getAll());

				request.setAttribute("ClassName", request.getParameter("ClassName"));
				request.setAttribute("MaxStudents", request.getParameter("MaxStudents"));
				request.setAttribute("CourseID", request.getParameter("CourseID"));
				request.setAttribute("SchoolYear", request.getParameter("SchoolYear"));
				request.setAttribute("IsActive", request.getParameter("IsActive") != null);

				request.getRequestDispatcher("/WEB-INF/admin/QLLopHoc/Create.jsp").forward(request, response);
				return; 
			}

		case "/Edit":
		    QLLopHoc lEdit = new QLLopHoc();
		    lEdit.setClassID(parseIntSafe(request.getParameter("ClassID"), 0));
		    lEdit.setClassName(request.getParameter("ClassName"));
		    lEdit.setGradeLevelId(parseIntSafe(request.getParameter("GradeLevelId"), 0));
		    lEdit.setCourseID(parseIntegerNullable(request.getParameter("CourseID")));
		    lEdit.setMaxStudents(parseIntSafe(request.getParameter("MaxStudents"), 0));
		    lEdit.setCurrentStudents(parseIntegerNullable(request.getParameter("CurrentStudents")));
		    lEdit.setSchoolYear(request.getParameter("SchoolYear"));
		    lEdit.setActive(request.getParameter("IsActive") != null);
		    dao.update(lEdit);
		    break;


		case "/ToggleStatus":
			int toggleId = parseIntSafe(request.getParameter("id"), 0);
			QLLopHoc lToggle = dao.getById(toggleId);
			if (lToggle != null) {
				lToggle.setActive(!lToggle.isActive());
				dao.update(lToggle);
			}
			break;

		case "/Delete":
			int deleteId = parseIntSafe(request.getParameter("ClassID"), 0);
			dao.delete(deleteId);
			break;
		}

		response.sendRedirect(request.getContextPath() + "/admin/QLLopHoc/Index");
	}

	private int parseIntSafe(String s, int defaultVal) {
		try {
			return (s != null && !s.isEmpty()) ? Integer.parseInt(s) : defaultVal;
		} catch (NumberFormatException e) {
			return defaultVal;
		}
	}

	private Integer parseIntegerNullable(String s) {
		try {
			return (s != null && !s.isEmpty()) ? Integer.parseInt(s) : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String computeSchoolYear(QLKhoaHoc khoaHoc, QLKhoi khoi, int index) {
		if (khoaHoc == null || khoaHoc.getStartYear() == null)
			return null;
		int startYear = khoaHoc.getStartYear() + index;
		return startYear + "-" + (startYear + 1);
	}

}
