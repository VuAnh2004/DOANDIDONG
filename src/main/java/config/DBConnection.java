package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Nên đặt thông tin cấu hình trong tệp properties riêng, nhưng giữ nguyên ở đây
    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=QLDIEMTHPT;encrypt=false";
    private static final String USER = "sa";
    private static final String PASS = "191004";


    public static Connection getConnection() {
        try {
            // Bước 1: Load Driver (Không bắt buộc với JDBC 4.0 trở đi nhưng là thói quen tốt)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // Bước 2: Thiết lập kết nối
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            // System.out.println("Kết nối CSDL thành công!"); // Có thể thêm log
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy Driver SQL Server.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi: Kết nối CSDL thất bại. Vui lòng kiểm tra URL, User, Pass.");
            e.printStackTrace();
        }
        return null; // Trả về null nếu có lỗi
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                // System.out.println("Đã đóng kết nối CSDL."); // Có thể thêm log
            } catch (SQLException e) {
                System.err.println("Lỗi: Không thể đóng kết nối CSDL.");
                e.printStackTrace();
            }
        }
    }
}