package controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import Utilities.Functions;
import config.DBConnection;

@WebServlet({ "/Login/*", "/login/*" })
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        /* ================= TOKEN LOGIN ================= */
        String token = request.getParameter("token");

        if (token == null || token.isEmpty()) {
            String newToken = Functions.generateSecureToken(24);
            response.sendRedirect(request.getContextPath() + "/Login?token=" + newToken);
            return;
        }

        // ===== SỬA LỖI TẠI ĐÂY: KHÔNG GHI ĐÈ TOKEN =====
        String sessionToken = (String) session.getAttribute("LoginToken");
        if (sessionToken == null) {
            session.setAttribute("LoginToken", token);
            sessionToken = token;
        }
        request.setAttribute("token", sessionToken);

        /* ================= LOGIN ATTEMPTS ================= */
        Integer loginAttempts = (Integer) session.getAttribute("LoginAttempts");
        if (loginAttempts == null) {
            loginAttempts = 0;
        }

        boolean showCaptcha = loginAttempts >= 3;
        request.setAttribute("showCaptcha", showCaptcha);

        if (showCaptcha) {
            String captchaText = (String) session.getAttribute("CaptchaText");
            if (captchaText == null) {
                captchaText = Functions.generateCaptchaText(5);
                session.setAttribute("CaptchaText", captchaText);
            }
            String captchaImage = Functions.generateCaptchaImageBase64(captchaText);
            request.setAttribute("captchaImage", captchaImage);
        }

        /* ================= ERROR MESSAGE ================= */
        // SỬA: lấy message từ PARAMETER chứ không phải attribute
        String message = request.getParameter("_message");
        request.setAttribute("_message", message != null ? message : "");

        /* ================= FORWARD ================= */
        request.getRequestDispatcher("/WEB-INF/Login/Index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Integer loginAttempts = (Integer) session.getAttribute("LoginAttempts");
        loginAttempts = (loginAttempts == null) ? 0 : loginAttempts;

        String username = request.getParameter("UserName");
        String password = request.getParameter("Password");
        String token = request.getParameter("token");
        String captchaInput = request.getParameter("captchaInput");

        String sessionToken = (String) session.getAttribute("LoginToken");

        /* ================= TOKEN CHECK ================= */
        if (token == null || sessionToken == null || !token.equals(sessionToken)) {
            redirectWithNewToken(session, response, "Token đăng nhập không hợp lệ!", loginAttempts);
            return;
        }

        /* ================= CAPTCHA ================= */
        if (loginAttempts >= 3) {
            String captchaText = (String) session.getAttribute("CaptchaText");
            if (captchaInput == null || captchaText == null ||
                    !captchaText.equalsIgnoreCase(captchaInput)) {
                session.setAttribute("LoginAttempts", loginAttempts + 1);
                redirectWithNewToken(session, response, "Captcha không đúng!", loginAttempts + 1);
                return;
            }
        }

        /* ================= DATABASE ================= */
        Account acc = null;
        List<String> roles = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT UserID, UserName, Email, Password, IsActive FROM Account WHERE UserName = ?")) {
                pst.setString(1, username);
                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        session.setAttribute("LoginAttempts", loginAttempts + 1);
                        redirectWithNewToken(session, response, "Tài khoản hoặc mật khẩu không đúng!", loginAttempts + 1);
                        return;
                    }

                    if (!Functions.verifyPassword(password, rs.getString("Password"))) {
                        session.setAttribute("LoginAttempts", loginAttempts + 1);
                        redirectWithNewToken(session, response, "Tài khoản hoặc mật khẩu không đúng!", loginAttempts + 1);
                        return;
                    }

                    if (!rs.getBoolean("IsActive")) {
                        redirectWithNewToken(session, response, "Tài khoản bị khóa!", loginAttempts);
                        return;
                    }

                    acc = new Account(
                            rs.getInt("UserID"),
                            rs.getString("UserName"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            true
                    );
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT r.RoleName FROM tblUsersRoles ur JOIN tblRoles r ON ur.RoleID = r.RoleID WHERE ur.UserID = ?")) {
                pst.setInt(1, acc.getUserId());
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        roles.add(rs.getString("RoleName"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            redirectWithNewToken(session, response, "Lỗi kết nối CSDL!", loginAttempts);
            return;
        }

        /* ================= LOGIN SUCCESS ================= */
        session.removeAttribute("LoginAttempts");
        session.removeAttribute("CaptchaText");
        session.removeAttribute("LoginToken");

        Functions.setUserSession(
                session,
                acc.getUserId(),
                acc.getUsername(),
                acc.getEmail(),
                String.join(",", roles)
        );

        if (roles.contains("Admin") || roles.contains("Teacher")) {
            response.sendRedirect(request.getContextPath() + "/admin");
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    private void redirectWithNewToken(HttpSession session, HttpServletResponse response,
                                      String message, int loginAttempts) throws IOException {
        String newToken = Functions.generateSecureToken(24);
        session.setAttribute("LoginToken", newToken);
        session.setAttribute("LoginAttempts", loginAttempts);
        response.sendRedirect(
                session.getServletContext().getContextPath()
                        + "/Login?token=" + newToken
                        + "&_message=" + java.net.URLEncoder.encode(message, "UTF-8")
        );
    }

    private static class Account {
        private int userId;
        private String username;
        private String email;
        private String password;
        private boolean active;

        public Account(int userId, String username, String email, String password, boolean active) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.password = password;
            this.active = active;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
    }
}
