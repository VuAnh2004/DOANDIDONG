package controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import Utilities.Functions;
import config.DBConnection;

@WebServlet({"/Login", "/login"})
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        // ===== TOKEN =====
        String token = request.getParameter("token");
        String sessionToken = (String) session.getAttribute("LoginToken");

        if (token == null || token.isEmpty()) {
            String newToken = Functions.generateSecureToken(24);
            response.sendRedirect(request.getContextPath() + "/Login?token=" + newToken);
            return;
        }

        // Chỉ set token nếu session chưa có
        if (sessionToken == null) {
            session.setAttribute("LoginToken", token);
        }
        request.setAttribute("token", token);

        // ===== LOGIN ATTEMPTS =====
        Integer loginAttempts = (Integer) session.getAttribute("LoginAttempts");
        if (loginAttempts == null) loginAttempts = 0;

        boolean showCaptcha = loginAttempts >= 3;
        request.setAttribute("showCaptcha", showCaptcha);

        // ===== CAPTCHA =====
        if (showCaptcha) {
            String captchaText = (String) session.getAttribute("CaptchaText");
            if (captchaText == null) {
                captchaText = Functions.generateCaptchaText(5);
                session.setAttribute("CaptchaText", captchaText);
            }
            String captchaImage = Functions.generateCaptchaImageBase64(captchaText);
            request.setAttribute("captchaImage", captchaImage);
        }

        // ===== ERROR MESSAGE =====
        String message = request.getParameter("_message");
        request.setAttribute("_message", message != null ? message : "");

        // ===== FORWARD JSP =====
        request.getRequestDispatcher("/WEB-INF/Login/Index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = request.getParameter("UserName");
        String password = request.getParameter("Password");
        String token = request.getParameter("token");
        String captchaInput = request.getParameter("captchaInput");

        String sessionToken = (String) session.getAttribute("LoginToken");
        Integer loginAttempts = (Integer) session.getAttribute("LoginAttempts");
        loginAttempts = loginAttempts == null ? 0 : loginAttempts;

        // ===== TOKEN VALIDATION =====
        if (token == null || !token.equals(sessionToken)) {
            redirectWithNewToken(session, response, "Token đăng nhập không hợp lệ!", loginAttempts);
            return;
        }

        // ===== CAPTCHA VALIDATION =====
        if (loginAttempts >= 3) {
            String captchaText = (String) session.getAttribute("CaptchaText");
            if (captchaText == null) {
                captchaText = Functions.generateCaptchaText(5);
                session.setAttribute("CaptchaText", captchaText);
            }
            if (captchaInput == null || !captchaText.equalsIgnoreCase(captchaInput)) {
                session.setAttribute("LoginAttempts", loginAttempts + 1);
                redirectWithNewToken(session, response, "Captcha không đúng!", loginAttempts + 1);
                return;
            }
        }

        // ===== CHECK USER/PASSWORD =====
        Account acc = null;
        List<String> roles = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT UserID, UserName, Email, Password, IsActive FROM Account WHERE UserName = ?")) {
                pst.setString(1, username);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String dbPassword = rs.getString("Password");

                        // VERIFY PASSWORD: dùng Functions.verifyPassword
                        if (!Functions.verifyPassword(password, dbPassword)) {
                            session.setAttribute("LoginAttempts", loginAttempts + 1);
                            redirectWithNewToken(session, response, "Tài khoản hoặc mật khẩu không đúng!", loginAttempts + 1);
                            return;
                        }

                        boolean isActive = rs.getBoolean("IsActive");
                        if (!isActive) {
                            redirectWithNewToken(session, response, "Tài khoản bị khóa!", loginAttempts);
                            return;
                        }

                        acc = new Account(rs.getInt("UserID"), rs.getString("UserName"), rs.getString("Email"),
                                dbPassword, isActive);
                    } else {
                        session.setAttribute("LoginAttempts", loginAttempts + 1);
                        redirectWithNewToken(session, response, "Tài khoản hoặc mật khẩu không đúng!", loginAttempts + 1);
                        return;
                    }
                }
            }

            // ===== GET ROLES =====
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT r.RoleName FROM tblUsersRoles ur JOIN tblRoles r ON ur.RoleID = r.RoleID WHERE ur.UserID = ?")) {
                pst.setInt(1, acc.getUserId());
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) roles.add(rs.getString("RoleName"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            redirectWithNewToken(session, response, "Lỗi kết nối CSDL!", loginAttempts);
            return;
        }

        // ===== LOGIN SUCCESS =====
        session.removeAttribute("LoginAttempts");
        session.removeAttribute("CaptchaText");
        session.removeAttribute("LoginToken");

        String roleStr = String.join(",", roles);
        Functions.setUserSession(session, acc.getUserId(), acc.getUsername(), acc.getEmail(), roleStr);

        boolean isAdmin = roles.contains("Admin");
        boolean isTeacher = roles.contains("Teacher");

        if (isAdmin || isTeacher) {
            response.sendRedirect(request.getContextPath() + "/admin");
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    private void redirectWithNewToken(HttpSession session, HttpServletResponse response, String message, int loginAttempts)
            throws IOException {
        String newToken = Functions.generateSecureToken(24);
        session.setAttribute("LoginToken", newToken);
        session.setAttribute("LoginAttempts", loginAttempts);
        String url = "/Login?token=" + newToken + "&_message=" + java.net.URLEncoder.encode(message, "UTF-8");
        response.sendRedirect(session.getServletContext().getContextPath() + url);
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
        public boolean isActive() { return active; }
    }
}
