import java.sql.*;

public class FixMenuEncoding {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String username = "tiros_test";
        String password = "tirostest#123";

        Connection conn = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Set Oracle JDBC properties for proper Chinese character handling
            java.util.Properties props = new java.util.Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("oracle.jdbc.defaultNChar", "true");

            conn = DriverManager.getConnection(url, props);
            conn.setAutoCommit(false);

            System.out.println("Connected to database");

            // Step 1: Delete existing menus
            System.out.println("\n[1/3] Deleting existing menu records...");
            String deleteSql = "DELETE FROM SYS_PERMISSION WHERE id IN (?, ?, ?, ?, ?, ?)";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setString(1, "tiros_basic_manage_parent");
            deleteStmt.setString(2, "tiros_production_notice");
            deleteStmt.setString(3, "tiros_quality_process");
            deleteStmt.setString(4, "tiros_quota_bom");
            deleteStmt.setString(5, "tiros_standard_process");
            deleteStmt.setString(6, "tiros_tech_manual");
            int deleted = deleteStmt.executeUpdate();
            System.out.println("Deleted " + deleted + " records");
            deleteStmt.close();

            // Step 2: Insert parent menu
            System.out.println("\n[2/3] Inserting parent menu...");
            String insertParentSql = "INSERT INTO SYS_PERMISSION (ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, " +
                "REDIRECT, MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON, IS_ROUTE, IS_LEAF, KEEP_ALIVE, " +
                "HIDDEN, DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, RULE_FLAG, STATUS, " +
                "INTERNAL_OR_EXTERNAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ?, SYSDATE, ?, ?, ?, ?)";

            PreparedStatement parentStmt = conn.prepareStatement(insertParentSql);
            parentStmt.setString(1, "tiros_basic_manage_parent");
            parentStmt.setString(2, null);
            parentStmt.setNString(3, "TIROS基础管理");  // Use setNString for Chinese
            parentStmt.setString(4, "/tiros/basic");
            parentStmt.setString(5, "layouts/RouteView");
            parentStmt.setString(6, null);
            parentStmt.setString(7, null);
            parentStmt.setInt(8, 0);
            parentStmt.setString(9, null);
            parentStmt.setString(10, null);
            parentStmt.setInt(11, 100);
            parentStmt.setInt(12, 0);
            parentStmt.setString(13, "tool");
            parentStmt.setInt(14, 1);
            parentStmt.setInt(15, 0);
            parentStmt.setInt(16, 0);
            parentStmt.setInt(17, 0);
            parentStmt.setNString(18, "TIROS基础数据管理");  // Use setNString for Chinese
            parentStmt.setString(19, "admin");
            parentStmt.setString(20, "admin");
            parentStmt.setInt(21, 0);
            parentStmt.setInt(22, 0);
            parentStmt.setString(23, "1");
            parentStmt.setInt(24, 0);
            parentStmt.executeUpdate();
            System.out.println("Parent menu inserted");
            parentStmt.close();

            // Step 3: Insert child menus
            System.out.println("\n[3/3] Inserting child menus...");
            String insertChildSql = "INSERT INTO SYS_PERMISSION (ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, " +
                "REDIRECT, MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON, IS_ROUTE, IS_LEAF, KEEP_ALIVE, " +
                "HIDDEN, DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, RULE_FLAG, STATUS, " +
                "INTERNAL_OR_EXTERNAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ?, SYSDATE, ?, ?, ?, ?)";

            PreparedStatement childStmt = conn.prepareStatement(insertChildSql);

            // 1. 生产通知单
            insertChildMenu(childStmt, "tiros_production_notice", "生产通知单", "/tiros/basic/production-notice",
                "tiros/basic/ProductionNotice", "ProductionNotice", 1, "file-text", "生产通知单管理");

            // 2. 质量过程
            insertChildMenu(childStmt, "tiros_quality_process", "质量过程", "/tiros/basic/quality-process",
                "tiros/basic/QualityProcess", "QualityProcess", 2, "bar-chart", "质量过程管理");

            // 3. 定额BOM
            insertChildMenu(childStmt, "tiros_quota_bom", "定额BOM", "/tiros/basic/quota-bom",
                "tiros/basic/QuotaBom", "QuotaBom", 3, "database", "定额BOM管理");

            // 4. 标准工艺
            insertChildMenu(childStmt, "tiros_standard_process", "标准工艺", "/tiros/basic/standard-process",
                "tiros/basic/StandardProcess", "StandardProcess", 4, "ordered-list", "标准工艺管理");

            // 5. 工艺电子手册
            insertChildMenu(childStmt, "tiros_tech_manual", "工艺电子手册", "/tiros/basic/tech-manual",
                "tiros/basic/TechManual", "TechManual", 5, "book", "工艺电子手册管理");

            childStmt.close();

            conn.commit();
            System.out.println("\nAll menus inserted successfully!");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void insertChildMenu(PreparedStatement stmt, String id, String name, String url,
                                       String component, String componentName, int sortNo, String icon, String description) throws SQLException {
        stmt.setString(1, id);
        stmt.setString(2, "tiros_basic_manage_parent");
        stmt.setNString(3, name);  // Use setNString for Chinese characters
        stmt.setString(4, url);
        stmt.setString(5, component);
        stmt.setString(6, componentName);
        stmt.setString(7, null);
        stmt.setInt(8, 1);
        stmt.setString(9, null);
        stmt.setString(10, "1");
        stmt.setInt(11, sortNo);
        stmt.setInt(12, 0);
        stmt.setString(13, icon);
        stmt.setInt(14, 1);
        stmt.setInt(15, 1);
        stmt.setInt(16, 1);
        stmt.setInt(17, 0);
        stmt.setNString(18, description);  // Use setNString for Chinese characters
        stmt.setString(19, "admin");
        stmt.setString(20, "admin");
        stmt.setInt(21, 0);
        stmt.setInt(22, 0);
        stmt.setString(23, "1");
        stmt.setInt(24, 0);
        stmt.executeUpdate();
        System.out.println("  - " + name + " inserted");
    }
}
