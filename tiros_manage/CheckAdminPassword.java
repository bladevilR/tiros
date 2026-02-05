import java.sql.*;

public class CheckAdminPassword {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String dbUser = "tiros_test";
        String dbPassword = "tirostest#123";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);

            String sql = "SELECT username, password, salt, realname, status FROM sys_user WHERE username = 'admin'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Admin user info from database:");
            System.out.println("=================================");
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String salt = rs.getString("salt");
                String realname = rs.getString("realname");
                int status = rs.getInt("status");

                System.out.println("Username: " + username);
                System.out.println("Password (encrypted): " + password);
                System.out.println("Salt: " + salt);
                System.out.println("Real name: " + realname);
                System.out.println("Status: " + status);
            } else {
                System.out.println("Admin user not found!");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
