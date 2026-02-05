import java.sql.*;

public class FindRelatedMenus {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String dbUser = "tiros_test";
        String dbPassword = "tirostest#123";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);

            // Search for any menus that might be related
            String sql = "SELECT id, parent_id, name, url, component FROM sys_permission WHERE LOWER(name) LIKE '%工艺%' OR LOWER(name) LIKE '%技术%' OR LOWER(name) LIKE '%手册%' OR LOWER(component) LIKE '%tech%'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Related menus found:");
            System.out.println("====================");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id"));
                System.out.println("Parent: " + rs.getString("parent_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("URL: " + rs.getString("url"));
                System.out.println("Component: " + rs.getString("component"));
                System.out.println();
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
