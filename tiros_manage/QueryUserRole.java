import java.sql.*;

public class QueryUserRole {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String username = "tiros_test";
        String password = "tirostest#123";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, username, password);

            // 查询testuser的角色
            String sql = "SELECT u.username, u.realname, r.role_name, r.id as role_id " +
                        "FROM sys_user u " +
                        "JOIN sys_user_role ur ON u.id = ur.user_id " +
                        "JOIN sys_role r ON ur.role_id = r.id " +
                        "WHERE u.username = 'testuser'";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("Test User角色信息：");
            System.out.println("----------------------------------------");
            while (rs.next()) {
                System.out.println("用户名: " + rs.getString("username"));
                System.out.println("真实姓名: " + rs.getString("realname"));
                System.out.println("角色名: " + rs.getString("role_name"));
                System.out.println("角色ID: " + rs.getString("role_id"));
                System.out.println("----------------------------------------");
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
