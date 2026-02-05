import java.sql.*;

public class QueryMenuContent {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String username = "tiros_test";
        String password = "tirostest#123";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, username, password);

            String sql = "SELECT id, name, perms, component " +
                        "FROM sys_permission " +
                        "WHERE id IN ('tiros_basic_manage_parent', 'tiros_production_notice', " +
                        "'tiros_quality_process', 'tiros_quota_bom', 'tiros_standard_process', 'tiros_tech_manual') " +
                        "ORDER BY sort_no";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("Menu Content Check:");
            System.out.println("========================================");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Perms: " + rs.getString("perms"));
                System.out.println("Component: " + rs.getString("component"));
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
