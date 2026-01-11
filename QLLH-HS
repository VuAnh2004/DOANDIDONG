package controller.admin;

import model.DAO.*;
import model.DAO.impl.*;
import model.bean.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet("/admin/QLHS_LH/*")
public class QLHS_LHController extends HttpServlet {

	private final QLHS_LHDAOImpl hs_lhDAO = new QLHS_LHDAOImpl();
	private final QLHocSinhDAOImpl hsDAO = new QLHocSinhDAOImpl();
	private final QLLopHocDAOImpl lopHocDAO = new QLLopHocDAOImpl();
	private final QLKhoaHocDAOImpl khoaHocDAO = new QLKhoaHocDAOImpl();
	private final QLHocKyDAOImpl hocKyDAO = new QLHocKyDAOImpl();
	private final AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();

	/* ---------------- MENU ---------------- */
	private void setMenu(HttpServletRequest req) {
		List<AdminMenu> flatMenus = adminMenuDAO.getActiveMenus();
		req.setAttribute("menus", buildMenuTree(flatMenus, req.getContextPath()));
	}

	private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
		Map<Integer, AdminMenu> map = new HashMap<>();
		for (AdminMenu m : flatMenus) {
			map.put((int) m.getAdminMenuID(), m);
			if (m.getIdName() == null || m.getIdName().isEmpty())
				m.setIdName("menu-" + m.getAdminMenuID());
			String controller = m.getControllerName();
			String action = m.getActionName();
			if (controller != null && action != null && !controller.isEmpty() && !action.isEmpty())
				m.setItemTarget(contextPath + "/admin/" + controller + "/" + action);
			else
				m.setItemTarget("#");
		}
		for (AdminMenu m : flatMenus) {
			if (m.getParentLevel() != 0) {
				AdminMenu parent = map.get(m.getParentLevel());
				if (parent != null) {
					if (parent.getSubMenus() == null)
						parent.setSubMenus(new ArrayList<>());
					parent.getSubMenus().add(m);
				}
			}
		}
		List<AdminMenu> roots = new ArrayList<>();
		for (AdminMenu m : flatMenus)
			if (m.getParentLevel() == 0)
				roots.add(m);
		return roots;
	}

	/* ---------------- FORWARD ---------------- */
	private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
			throws ServletException, IOException {
		setMenu(req);
		req.setAttribute("contentPage", view);
		req.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(req, resp);
	}

	/* ---------------- LOAD DROPDOWNS ---------------- */
	private void loadDropdowns(HttpServletRequest req) {
		List<QLHocSinh> students = hsDAO.getAll();
		List<QLLopHoc> classes = lopHocDAO.getAll();
		List<QLKhoaHoc> courses = khoaHocDAO.getAll();
		List<QLHocKy> semesters = hocKyDAO.getAll();

		req.setAttribute("studentList", students);
		req.setAttribute("classList", classes);
		req.setAttribute("courseList", courses);
		req.setAttribute("semesterList", semesters);
	}

	/* ---------------- GET ACTION ---------------- */
	private String getAction(HttpServletRequest req) {
		String path = req.getPathInfo();
		if (path == null || path.equals("/"))
			return "Index";
		path = path.replace("/", "");
		if (path.isEmpty())
			return "Index";
		return path.substring(0, 1).toUpperCase() + path.substring(1);
	}

	/* ---------------- GET ---------------- */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Set menu
		setMenu(req);

		String action = getAction(req);

		switch (action) {
		case "Create":
			// Load dropdowns cho form
			loadDropdowns(req);
			// Forward tới JSP tạo mới
			forward(req, resp, "/WEB-INF/admin/QLHS_LH/Create.jsp");
			break;

		case "Edit":
			handleEditGet(req, resp);
			break;

		case "Delete":
			handleDeleteGet(req, resp);
			break;

		default:
			// List tất cả học sinh lớp học
			req.setAttribute("hs_lhList", hs_lhDAO.getAll());
			forward(req, resp, "/WEB-INF/admin/QLHS_LH/Index.jsp");
			break;
		}
	}

	private void handleEditGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer id = parseInt(req.getParameter("id"));
		if (id == null) {
			resp.sendRedirect(req.getContextPath() + "/admin/QLHS_LH/Index");
			return;
		}
		QLHS_LH rec = hs_lhDAO.getById(id);
		if (rec == null) {
			resp.sendRedirect(req.getContextPath() + "/admin/QLHS_LH/Index");
			return;
		}
		req.setAttribute("hs_lh", rec);
		loadDropdowns(req);
		forward(req, resp, "/WEB-INF/admin/QLHS_LH/Edit.jsp");
	}

	private void handleDeleteGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Integer id = parseInt(req.getParameter("id"));
		if (id == null) {
			resp.sendRedirect(req.getContextPath() + "/admin/QLHS_LH/Index");
			return;
		}
		req.setAttribute("hs_lh", hs_lhDAO.getById(id));
		forward(req, resp, "/WEB-INF/admin/QLHS_LH/Delete.jsp");
	}

	/* ---------------- POST ---------------- */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		switch (getAction(req)) {
		case "Create":
			createBulk(req);
			break;
		case "Edit":
			update(req);
			break;
		case "Delete":
			delete(req);
			break;
		case "ToggleStatus":
			toggleStatus(req);
			break;
		}

		resp.sendRedirect(req.getContextPath() + "/admin/QLHS_LH/Index");
	}

	private void createBulk(HttpServletRequest req) {
	    // Lấy classID và courseID
	    Integer classID = parseInt(req.getParameter("ClassID"));
	    Integer courseID = parseInt(req.getParameter("CourseID"));
	    if (classID == null || courseID == null)
	        return;

	    // Lấy thông tin lớp
	    QLLopHoc lop = lopHocDAO.getById(classID);
	    if (lop == null)
	        return;

	    int maxStudents = lop.getMaxStudents();
	    int currentStudents = lop.getCurrentStudents() != null ? lop.getCurrentStudents() : 0;
	    int availableSeats = maxStudents - currentStudents;
	    if (availableSeats <= 0)
	        return;

	    // Lấy danh sách học kỳ
	    List<QLHocKy> semesters = hocKyDAO.getAll();
	    if (semesters.isEmpty())
	        return;

	    // Lấy danh sách học sinh và trạng thái
	    String[] studentIDs = req.getParameterValues("StudentID[]");
	    String[] isActiveValues = req.getParameterValues("IsActive"); // hidden input
	    if (studentIDs == null || studentIDs.length == 0)
	        return;

	    int addedCount = 0;

	    for (int i = 0; i < studentIDs.length; i++) {
	        String studentID = studentIDs[i];
	        boolean isActive = false;
	        if (isActiveValues != null && i < isActiveValues.length) {
	            isActive = "true".equalsIgnoreCase(isActiveValues[i]);
	        }

	        if (studentID == null || studentID.isEmpty())
	            continue;

	        boolean insertedAtLeastOnce = false;

	        // Thêm học sinh vào tất cả học kỳ nếu chưa tồn tại
	        for (QLHocKy hk : semesters) {
	            if (!hs_lhDAO.existsInClassSemester(studentID, classID, hk.getSemesterId(), courseID)) {
	                QLHS_LH item = new QLHS_LH();
	                item.setStudentID(studentID);
	                item.setClassID(classID);
	                item.setCourseID(courseID);
	                item.setSemesterID(hk.getSemesterId());
	                item.setIsActive(isActive);
	                hs_lhDAO.insert(item);
	                insertedAtLeastOnce = true;
	            }
	        }

	        if (insertedAtLeastOnce)
	            addedCount++;

	        // Dừng nếu lớp đã đầy
	        if (addedCount >= availableSeats)
	            break;
	    }

	    // Cập nhật số học sinh hiện tại của lớp
	    if (addedCount > 0) {
	        lopHocDAO.updateCurrentStudents(classID, currentStudents + addedCount);
	    }
	}

	/* ---------------- UPDATE ---------------- */
	private void update(HttpServletRequest req) {
		Integer id = parseInt(req.getParameter("HocSinhLopHocID"));
		if (id == null)
			return;
		QLHS_LH x = new QLHS_LH();
		x.setHocSinhLopHocID(id);
		x.setStudentID(req.getParameter("StudentID"));
		x.setClassID(parseInt(req.getParameter("ClassID")));
		x.setCourseID(parseInt(req.getParameter("CourseID")));
		x.setSemesterID(parseInt(req.getParameter("SemesterID")));
		x.setIsActive(req.getParameter("IsActive") != null);
		hs_lhDAO.update(x);
	}

	/* ---------------- DELETE ---------------- */
	private void delete(HttpServletRequest req) {
		Integer id = parseInt(req.getParameter("HocSinhLopHocID"));
		if (id != null) {
			QLHS_LH rec = hs_lhDAO.getById(id); // Lấy bản ghi trước khi xóa
			if (rec != null) {
				hs_lhDAO.delete(id); // Xóa học sinh khỏi lớp

				// Cập nhật số học sinh hiện tại của lớp
				int currentCount = hs_lhDAO.countUniqueStudentsInClass(rec.getClassID());
				lopHocDAO.updateCurrentStudents(rec.getClassID(), currentCount);
			}
		}
	}

	/* ---------------- TOGGLE STATUS ---------------- */
	private void toggleStatus(HttpServletRequest req) {
		Integer id = parseInt(req.getParameter("id"));
		if (id == null)
			return;
		QLHS_LH rec = hs_lhDAO.getById(id);
		if (rec != null) {
			rec.setIsActive(!rec.getIsActive());
			hs_lhDAO.update(rec);
		}
	}

	/* ---------------- HELPER ---------------- */
	private Integer parseInt(String text) {
		try {
			return (text == null || text.isBlank()) ? null : Integer.parseInt(text);
		} catch (Exception e) {
			return null;
		}
	}

}
