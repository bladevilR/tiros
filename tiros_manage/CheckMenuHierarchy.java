import java.sql.*;

public class CheckMenuHierarchy {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String dbUser = "tiros_test";
        String dbPassword = "tirostest#123";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);

            // Check parent menu
            String parentSql = "SELECT id, name FROM sys_permission WHERE id = 'tiros_basic_manage_parent'";
            PreparedStatement parentStmt = conn.prepareStatement(parentSql);
            ResultSet parentRs = parentStmt.executeQuery();
            if (parentRs.next()) {
                System.out.println("Parent menu exists: " + parentRs.getString("id") + " - " + parentRs.getString("name"));
            } else {
                System.out.println("ERROR: Parent menu 'tiros_basic_manage_parent' NOT FOUND!");
            }
            parentRs.close();
            parentStmt.close();

            // Check child menus
            System.out.println("\nChild menus:");
            String childSql = "SELECT id, parent_id, name, url, component, is_route FROM sys_permission WHERE parent_id = 'tiros_basic_manage_parent' ORDER BY sort_no";
            PreparedStatement childStmt = conn.prepareStatement(childSql);
            ResultSet childRs = childStmt.executeQuery();

            int count = 0;
            while (childRs.next()) {
                count++;
                System.out.println(count + ". ID: " + childRs.getString("id"));
                System.out.println("   Name: " + childRs.getString("name"));
                System.out.println("   URL: " + childRs.getString("url"));
                System.out.println("   Component: " + childRs.getString("component"));
                System.out.println("   IsRoute: " + childRs.getInt("is_route"));
                System.out.println();
            }

            if (count == 0) {
                System.out.println("ERROR: No child menus found under parent!");
            }

            childRs.close();
            childStmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
