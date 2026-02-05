import java.sql.*;

public class CheckMenus {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String dbUser = "tiros_test";
        String dbPassword = "tirostest#123";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);

            String sql = "SELECT id, name, url, component FROM sys_permission WHERE id IN ('tiros_production_notice', 'tiros_quality_process', 'tiros_quota_bom', 'tiros_standard_process', 'tiros_tech_manual') OR name LIKE '%生产通知%' OR name LIKE '%质量%' OR name LIKE '%定额%' OR name LIKE '%标准工序%' OR name LIKE '%工艺手册%'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Menus in database:");
            System.out.println("==================");
            int count = 0;
            while (rs.next()) {
                count++;
                String id = rs.getString("id");
                String name = rs.getString("name");
                String urlPath = rs.getString("url");
                String component = rs.getString("component");
                System.out.println(count + ". ID: " + id);
                System.out.println("   Name: " + name);
                System.out.println("   URL: " + urlPath);
                System.out.println("   Component: " + component);
                System.out.println();
            }

            if (count == 0) {
                System.out.println("No menus found! Need to execute SQL.");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
