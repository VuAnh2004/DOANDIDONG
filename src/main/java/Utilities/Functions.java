
package Utilities;

import jakarta.servlet.http.HttpSession;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class Functions {
    /* ================= USER SESSION ================= */
    public static int USER_ID = 0;
    public static String USERNAME = "";
    public static String EMAIL = "";
    public static String ROLE = "";

    /* ================= MD5 PASSWORD ================= */
    public static String MD5Hash(String input) {
        if (input == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String MD5Password(String password) {
        String hashed = MD5Hash(password);
        for (int i = 0; i <= 5; i++) {
            hashed = MD5Hash(hashed + hashed);
        }
        return hashed;
    }

    public static String hashPassword(String password) { return MD5Password(password); }

    public static boolean verifyPassword(String raw, String hashed) {
        return MD5Password(raw).equalsIgnoreCase(hashed);
    }

    /* ================= LOGIN ================= */
    public static boolean isLogin() {
        return USER_ID > 0 && !USERNAME.isEmpty() && !EMAIL.isEmpty();
    }

    public static void setUserSession(HttpSession session,
                                      int userId,
                                      String username,
                                      String email,
                                      String role) {
        session.setAttribute("USER_ID", userId);
        session.setAttribute("USERNAME", username);
        session.setAttribute("EMAIL", email);
        session.setAttribute("ROLE", role);

        USER_ID = userId;
        USERNAME = username;
        EMAIL = email;
        ROLE = role;
    }

    public static void logout(HttpSession session) {
        session.invalidate();
        USER_ID = 0;
        USERNAME = "";
        EMAIL = "";
        ROLE = "";
    }

    /* ================= ROLE ================= */
    public static boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("ROLE");
        return role != null && role.toString().contains("Admin");
    }

    public static boolean isTeacher(HttpSession session) {
        Object role = session.getAttribute("ROLE");
        return role != null && role.toString().contains("Teacher");
    }

    public static boolean isStudent(HttpSession session) {
        Object role = session.getAttribute("ROLE");
        return role != null && role.toString().contains("Student");
    }

    /* ================= TOKEN ================= */
    public static String generateSecureToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /* ================= TIME AGO ================= */
    public static String timeAgo(LocalDateTime time) {
        Duration d = Duration.between(time, LocalDateTime.now());
        if (d.toSeconds() < 60) return "Vừa xong";
        if (d.toMinutes() < 60) return d.toMinutes() + " phút trước";
        if (d.toHours() < 24) return d.toHours() + " giờ trước";
        if (d.toDays() < 7) return d.toDays() + " ngày trước";
        return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /* ================= CAPTCHA ================= */
    public static String generateCaptchaText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) sb.append(chars.charAt(rand.nextInt(chars.length())));
        return sb.toString();
    }

    public static String generateCaptchaImageBase64(String text) {
        try {
            int width = 120, height = 40;
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(text, 10, 28);

            // Thêm nhiễu
            Random r = new Random();
            for (int i = 0; i < 30; i++) {
                int x = r.nextInt(width);
                int y = r.nextInt(height);
                g.drawRect(x, y, 1, 1);
            }
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
