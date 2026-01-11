package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.QLGiaoVienDAO;
import model.DAO.QLMonHocDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.QLGiaoVienDAOImpl;
import model.DAO.impl.QLMonHocDAOImpl;
import model.bean.AdminMenu;
import model.bean.QLGiaoVien;
import model.bean.QLMonHoc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.*;

@WebServlet("/admin/QLGiaoVien/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 15)
public class QLGiaoVienController extends HttpServlet {

	private QLGiaoVienDAO dao = new QLGiaoVienDAOImpl();
	private QLMonHocDAO monHocDAO = new QLMonHocDAOImpl();
	private AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();
	private static final String UPLOAD_DIR = "uploads";

	// ===== Build menu sidebar =====
	private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
		List<AdminMenu> allMenus = new ArrayList<>(flatMenus);
		Map<Integer, AdminMenu> menuMap = new HashMap<>();
		for (AdminMenu m : allMenus) {
			menuMap.put((int) m.getAdminMenuID(), m);
			if (m.getIdName() == null || m.getIdName().isEmpty())
				m.setIdName("menu-" + m.getAdminMenuID());

			String controller = m.getControllerName();
			String action = m.getActionName();
			if (m.getAdminMenuID() == 1 || ("Home".equalsIgnoreCase(controller) && "Index".equalsIgnoreCase(action))) {
				m.setItemTarget(contextPath + "/admin");
			} else if (controller != null && action != null && !controller.isEmpty() && !action.isEmpty()) {
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
			if (m.getParentLevel() == 0)
				rootMenus.add(m);
		}
		return rootMenus;
	}

	// =========================================================
	// GET METHOD
	// =========================================================
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Load menu
		List<AdminMenu> menus = adminMenuDAO.getActiveMenus();
		request.setAttribute("menus", buildMenuTree(menus, request.getContextPath()));

		String action = request.getPathInfo();
		if (action == null || action.equals("/"))
			action = "/Index";

		switch (action) {
		case "/Index":
			request.setAttribute("list", dao.getAll());
			request.setAttribute("contentPage", "/WEB-INF/admin/QLGiaoVien/Index.jsp");
			request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
			break;

		case "/Create":
			request.setAttribute("subjects", monHocDAO.getAll());
			request.setAttribute("giaoVien", null);
			request.setAttribute("contentPage", "/WEB-INF/admin/QLGiaoVien/Create.jsp");
			request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
			break;

		

		case "/Edit":
		    try {
		        int id = Integer.parseInt(request.getParameter("id"));
		        QLGiaoVien gv = dao.getById(id);

		        if (gv == null) {
		            response.sendRedirect(request.getContextPath() + "/admin/QLGiaoVien/Index");
		            return;
		        }

		        // Lấy danh sách môn giáo viên đang dạy
		        List<QLMonHoc> assigned = dao.getSubjectsByTeacherID(gv.getTeacherID());
		        Set<Integer> assignedSubjectIDs = new HashSet<>();
		        for (QLMonHoc mh : assigned) {
		            assignedSubjectIDs.add(mh.getSubjectID());
		        }

		        request.setAttribute("assignedSubjectIDs", assignedSubjectIDs);
		        request.setAttribute("giaoVien", gv);
		        request.setAttribute("subjects", monHocDAO.getAll());
		        request.setAttribute("contentPage", "/WEB-INF/admin/QLGiaoVien/Edit.jsp");
		        request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);

		    } catch (Exception e) {
		        response.sendRedirect(request.getContextPath() + "/admin/QLGiaoVien/Index");
		    }
		    break;


		case "/Delete":
			try {
				int idDel = Integer.parseInt(request.getParameter("id"));
				request.setAttribute("giaoVien", dao.getById(idDel));
				request.setAttribute("contentPage", "/WEB-INF/admin/QLGiaoVien/Delete.jsp");
				request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
			} catch (NumberFormatException e) {
				response.sendRedirect(request.getContextPath() + "/admin/QLGiaoVien/Index");
			}
			break;

		default:
			response.sendRedirect(request.getContextPath() + "/admin/QLGiaoVien/Index");
		}
	}

	// =========================================================
	// POST METHOD
	// =========================================================
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String action = request.getPathInfo();
		if (action == null)
			action = "";

		switch (action) {
		case "/Create":
			handleCreate(request);
			break;
		case "/Edit":
			handleEdit(request);
			break;
		case "/Delete":
			handleDelete(request);
			break;
		case "/ToggleStatus":
			handleToggleStatus(request);
			break;
		}
		response.sendRedirect(request.getContextPath() + "/admin/QLGiaoVien/Index");
	}

	// =========================================================
	// HÀM XỬ LÝ RIÊNG
	// =========================================================

	private void handleCreate(HttpServletRequest request) throws IOException, ServletException {
		QLGiaoVien gv = extractGiaoVienFromRequest(request, null);
		dao.insert(gv);
	}

	private void handleEdit(HttpServletRequest request) throws IOException, ServletException {
		try {
			int id = Integer.parseInt(request.getParameter("ID"));
			QLGiaoVien existing = dao.getById(id);
			if (existing == null)
				return;

			QLGiaoVien updated = extractGiaoVienFromRequest(request, existing);
			dao.update(updated);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void handleDelete(HttpServletRequest request) {
		try {
			int id = Integer.parseInt(request.getParameter("ID"));
			QLGiaoVien gv = dao.getById(id);
			if (gv != null && gv.getImages() != null)
				deleteFile(gv.getImages(), request);
			dao.delete(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void handleToggleStatus(HttpServletRequest request) {
		try {
			int id = Integer.parseInt(request.getParameter("id"));
			QLGiaoVien gv = dao.getById(id);
			if (gv != null) {
				gv.setIsActive(!gv.isIsActive());
				dao.update(gv);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	// Extract object from form
	private QLGiaoVien extractGiaoVienFromRequest(HttpServletRequest request, QLGiaoVien existing)
			throws IOException, ServletException {
		QLGiaoVien gv = existing != null ? existing : new QLGiaoVien();

		gv.setTeacherID(request.getParameter("TeacherID"));
		gv.setFullName(request.getParameter("FullName"));
		gv.setGender(request.getParameter("Gender"));
		String birthStr = request.getParameter("Birth");
		if (birthStr != null && !birthStr.isEmpty())
			gv.setBirth(Date.valueOf(birthStr));
		else if (existing != null)
			gv.setBirth(existing.getBirth());

		gv.setAddress(request.getParameter("Address"));
		gv.setNation(request.getParameter("Nation"));
		gv.setReligion(request.getParameter("Religion"));
		gv.setStatusTeacher(request.getParameter("StatusTeacher"));
		gv.setCCCD(request.getParameter("CCCD"));
		gv.setGroupDV(request.getParameter("GroupDV"));
		gv.setNumberPhone(request.getParameter("NumberPhone"));
		gv.setNumberBHXH(request.getParameter("NumberBHXH"));
		gv.setIsActive(request.getParameter("IsActive") != null);

		// SubjectIDs (checkbox danh sách môn học)
		String[] subjectIDs = request.getParameterValues("SubjectIDs");

		if (subjectIDs != null) {
			// Người dùng có tick môn
			List<Integer> list = new ArrayList<>();
			for (String s : subjectIDs)
				list.add(Integer.parseInt(s));
			gv.setSubjectIDs(list);
		} else {
			// Không tick gì → KHÔNG XÓA môn học → giữ danh sách cũ
			if (existing != null) {
				gv.setSubjectIDs(existing.getSubjectIDs());
			} else {
				gv.setSubjectIDs(new ArrayList<>());
			}
		}

		// Images
		Part filePart = request.getPart("Images");
		String deleteImage = request.getParameter("DeleteImage");
		boolean needDeleteOldFile = "true".equals(deleteImage);
		boolean isNewFileUploaded = (filePart != null && filePart.getSize() > 0);

		if ((needDeleteOldFile || isNewFileUploaded) && gv.getImages() != null) {
			deleteFile(gv.getImages(), request);
			gv.setImages(null);
		}

		if (isNewFileUploaded) {
			String fileName = saveFile(filePart, request);
			gv.setImages(fileName);
		}

		return gv;
	}

	private String saveFile(Part filePart, HttpServletRequest request) throws IOException {
		String filename = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		if (filename == null || filename.isEmpty())
			return null;

		String uploadPath = getServletContext().getRealPath("/" + UPLOAD_DIR);
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists())
			uploadDir.mkdirs();

		String fileName = System.currentTimeMillis() + "_" + filename;
		filePart.write(uploadPath + File.separator + fileName);
		return UPLOAD_DIR + "/" + fileName;
	}

	private void deleteFile(String relativePath, HttpServletRequest request) {
		if (relativePath != null && !relativePath.isEmpty()) {
			String filePath = getServletContext().getRealPath("/") + relativePath;
			File file = new File(filePath);
			if (file.exists())
				file.delete();
		}
	}
}
