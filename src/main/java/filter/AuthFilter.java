

package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter("/admin/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        /* ================= CHƯA ĐĂNG NHẬP ================= */
        if (session == null || session.getAttribute("USER_ID") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        /* ================= LẤY ROLE ================= */
        String role = (String) session.getAttribute("ROLE");
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        /* ================= STUDENT → CHẶN ADMIN ================= */
        if (role.contains("Student")) {
            request.setAttribute("error", "Bạn không có quyền truy cập khu vực quản trị!");
            request.getRequestDispatcher("/WEB-INF/Auth/Index.jsp")
                   .forward(request, response);
            return;
        }

        /* ================= ADMIN / TEACHER → OK ================= */
        if (role.contains("Admin") || role.contains("Teacher")) {
            chain.doFilter(req, res);
            return;
        }

        /* ================= ROLE KHÁC → LOGIN ================= */
        response.sendRedirect(request.getContextPath() + "/Login");
    }
}

