package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.QLHocSinhDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.DAO.impl.QLHocSinhDAOImpl;
import model.bean.AdminMenu;
import model.bean.QLHocSinh;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.*;

@WebServlet("/admin/QLHocSinh/*")
// Thiết lập MultipartConfig cho phép xử lý upload file ảnh
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 15)
public class QLHocSinhController extends HttpServlet {

	private QLHocSinhDAO dao = new QLHocSinhDAOImpl();
	private AdminMenuDAO adminMenuDAO = new AdminMenuDAOImpl();
    
    // Đường dẫn gốc của thư mục ảnh (sẽ là /uploads)
    private static final String UPLOAD_DIR = "uploads";

	// ===== Build sidebar menu (Giữ nguyên logic từ QLHocKy) =====
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
	//                         GET METHOD
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
			request.setAttribute("contentPage", "/WEB-INF/admin/QLHocSinh/Index.jsp");
			request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
			break;
		case "/Create":
			request.setAttribute("contentPage", "/WEB-INF/admin/QLHocSinh/Create.jsp");
			request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
			break;
		case "/Edit":
			try {
				int idEdit = Integer.parseInt(request.getParameter("id"));
				request.setAttribute("hocSinh", dao.getById(idEdit));
				request.setAttribute("contentPage", "/WEB-INF/admin/QLHocSinh/Edit.jsp");
				request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
			} catch (NumberFormatException e) {
				response.sendRedirect(request.getContextPath() + "/admin/QLHocSinh/Index");
			}
			break;
		case "/Delete":
            // Chuyển sang POST để xử lý xóa thực tế, GET chỉ dùng để hiển thị form xác nhận
            try {
                int idDel = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("hocSinh", dao.getById(idDel));
                request.setAttribute("contentPage", "/WEB-INF/admin/QLHocSinh/Delete.jsp");
                request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/QLHocSinh/Index");
            }
			break;
            
		default:
			response.sendRedirect(request.getContextPath() + "/admin/QLHocSinh/Index");
		}
	}

	// =========================================================
	//                         POST METHOD
	// =========================================================
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
        // Fix: Đặt request encoding để lấy tiếng Việt chính xác
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
		response.sendRedirect(request.getContextPath() + "/admin/QLHocSinh/Index");
	}

    // =========================================================
	//                     HÀM XỬ LÝ RIÊNG
	// =========================================================
	
	// ===== Xử lý Create =====
	private void handleCreate(HttpServletRequest request) throws IOException, ServletException {
		QLHocSinh hs = new QLHocSinh();
		hs.setStudentID(request.getParameter("StudentID"));
		hs.setFullName(request.getParameter("FullName"));
		hs.setGender(request.getParameter("Gender"));
		String birthStr = request.getParameter("Birth");
		if (birthStr != null && !birthStr.isEmpty())
			hs.setBirth(Date.valueOf(birthStr));
		hs.setAddress(request.getParameter("Address"));
		hs.setNation(request.getParameter("Nation"));
		hs.setReligion(request.getParameter("Religion"));
		hs.setStatusStudent(request.getParameter("StatusStudent"));
		hs.setNumberPhone(request.getParameter("NumberPhone"));
		hs.setIsActive(request.getParameter("IsActive") != null);

		Part filePart = request.getPart("Images");
		if (filePart != null && filePart.getSize() > 0) {
			String fileName = saveFile(filePart, request);
			if (fileName != null)
				hs.setImages(fileName);
		}

		dao.insert(hs);
	}

	// ===== Xử lý Edit (Cập nhật logic xử lý ảnh) =====
	private void handleEdit(HttpServletRequest request) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("ID"));
            QLHocSinh hs = dao.getById(id);
            if (hs == null)
                return;

            hs.setStudentID(request.getParameter("StudentID"));
            hs.setFullName(request.getParameter("FullName"));
            hs.setGender(request.getParameter("Gender"));
            String birthStr = request.getParameter("Birth");
            if (birthStr != null && !birthStr.isEmpty())
                hs.setBirth(Date.valueOf(birthStr));
            else
                hs.setBirth(null); // Cho phép ngày sinh là null

            hs.setAddress(request.getParameter("Address"));
            hs.setNation(request.getParameter("Nation"));
            hs.setReligion(request.getParameter("Religion"));
            hs.setStatusStudent(request.getParameter("StatusStudent"));
            hs.setNumberPhone(request.getParameter("NumberPhone"));
            hs.setIsActive(request.getParameter("IsActive") != null);

            Part filePart = request.getPart("Images");
            String deleteImage = request.getParameter("DeleteImage"); // giá trị hidden từ form Edit JSP

            // 1. Kiểm tra xóa file cũ
            boolean needDeleteOldFile = "true".equals(deleteImage); // Người dùng nhấn nút Xóa Ảnh
            boolean isNewFileUploaded = (filePart != null && filePart.getSize() > 0);
            
            // Nếu có file mới hoặc có lệnh xóa, thì xóa file cũ
            if ((needDeleteOldFile || isNewFileUploaded) && hs.getImages() != null && !hs.getImages().isEmpty()) {
                deleteFile(hs.getImages(), request);
                hs.setImages(null); // Xóa đường dẫn cũ trong DB
            }
            
            // 2. Nếu chọn ảnh mới, lưu file và cập nhật đường dẫn
            if (isNewFileUploaded) {
                String fileName = saveFile(filePart, request);
                if (fileName != null)
                    hs.setImages(fileName);
            }
            
            // Nếu lệnh xóa được thực thi, Images đã là null (hoặc file mới đã được set)
            
            dao.update(hs);
            
        } catch (NumberFormatException e) {
             // Log lỗi nếu ID không hợp lệ
        }
	}
    
    // ===== Xử lý Delete (Xóa record và file ảnh liên quan) =====
    private void handleDelete(HttpServletRequest request) throws IOException, ServletException {
        try {
            int idDelete = Integer.parseInt(request.getParameter("ID"));
            QLHocSinh hsDelete = dao.getById(idDelete);
            
            if (hsDelete != null) {
                // Xóa ảnh trên server nếu có
                if (hsDelete.getImages() != null && !hsDelete.getImages().isEmpty()) {
                    deleteFile(hsDelete.getImages(), request);
                }
                // Xóa bản ghi trong DB
                dao.delete(idDelete);
            }
        } catch (NumberFormatException e) {
             // Log lỗi nếu ID không hợp lệ
        }
    }
    
    // ===== Xử lý Toggle Status =====
    private void handleToggleStatus(HttpServletRequest request) throws IOException, ServletException {
        try {
            int idToggle = Integer.parseInt(request.getParameter("id"));
            QLHocSinh hsToggle = dao.getById(idToggle);
            if (hsToggle != null) {
                hsToggle.setIsActive(!hsToggle.isIsActive());
                dao.update(hsToggle);
            }
        } catch (NumberFormatException e) {
             // Log lỗi nếu ID không hợp lệ
        }
    }
    

	// ===== Lưu file ảnh (Chuyển thành phương thức chung) =====
	private String saveFile(Part filePart, HttpServletRequest request) throws IOException {
		String filename = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		if (filename == null || filename.isEmpty())
			return null;

		// Lưu vào folder /uploads trong deployed webapp
		String uploadPath = getServletContext().getRealPath("/" + UPLOAD_DIR);
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists())
			uploadDir.mkdirs();

		String fileName = System.currentTimeMillis() + "_" + filename;
		filePart.write(uploadPath + File.separator + fileName);
		return UPLOAD_DIR + "/" + fileName; // lưu đường dẫn tương đối trong DB
	}
    
    // ===== Xóa file ảnh (Phương thức chung) =====
    private void deleteFile(String relativePath, HttpServletRequest request) {
        if (relativePath != null && !relativePath.isEmpty()) {
            String filePath = getServletContext().getRealPath("/") + relativePath;
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}