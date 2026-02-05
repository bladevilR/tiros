import java.sql.*;

public class FixMenuWithUnicode {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String username = "tiros_test";
        String password = "tirostest#123";

        Connection conn = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, username, password);
            conn.setAutoCommit(false);

            System.out.println("Connected to database");

            // Delete existing menus
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

            // Use direct SQL with UNISTR function
            System.out.println("\n[2/3] Inserting menus using UNISTR...");
            Statement stmt = conn.createStatement();

            // Parent menu: TIROS基础管理
            String sql1 = "INSERT INTO SYS_PERMISSION (ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, " +
                "REDIRECT, MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON, IS_ROUTE, IS_LEAF, KEEP_ALIVE, " +
                "HIDDEN, DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, RULE_FLAG, STATUS, " +
                "INTERNAL_OR_EXTERNAL) VALUES (" +
                "'tiros_basic_manage_parent', NULL, " +
                "UNISTR('TIROS\\57FA\\7840\\7BA1\\7406'), " +  // TIROS基础管理
                "'/tiros/basic', 'layouts/RouteView', NULL, NULL, 0, NULL, NULL, 100, 0, 'tool', 1, 0, 0, 0, " +
                "UNISTR('TIROS\\57FA\\7840\\6570\\636E\\7BA1\\7406'), " +  // TIROS基础数据管理
                "'admin', SYSDATE, 'admin', SYSDATE, 0, 0, '1', 0)";
            stmt.execute(sql1);
            System.out.println("Parent menu inserted");

            System.out.println("\n[3/3] Inserting child menus...");

            // 1. 生产通知单
            String sql2 = "INSERT INTO SYS_PERMISSION (ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, " +
                "REDIRECT, MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON, IS_ROUTE, IS_LEAF, KEEP_ALIVE, " +
                "HIDDEN, DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, RULE_FLAG, STATUS, " +
                "INTERNAL_OR_EXTERNAL) VALUES (" +
                "'tiros_production_notice', 'tiros_basic_manage_parent', " +
                "UNISTR('\\751F\\4EA7\\901A\\77E5\\5355'), " +  // 生产通知单
                "'/tiros/basic/production-notice', 'tiros/basic/ProductionNotice', 'ProductionNotice', NULL, 1, NULL, '1', 1, 0, 'file-text', 1, 1, 1, 0, " +
                "UNISTR('\\751F\\4EA7\\901A\\77E5\\5355\\7BA1\\7406'), " +  // 生产通知单管理
                "'admin', SYSDATE, 'admin', SYSDATE, 0, 0, '1', 0)";
            stmt.execute(sql2);
            System.out.println("  - Production Notice inserted");

            // 2. 质量过程
            String sql3 = "INSERT INTO SYS_PERMISSION (ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, " +
                "REDIRECT, MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON, IS_ROUTE, IS_LEAF, KEEP_ALIVE, " +
                "HIDDEN, DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, RULE_FLAG, STATUS, " +
                "INTERNAL_OR_EXTERNAL) VALUES (" +
                "'tiros_quality_process', 'tiros_basic_manage_parent', " +
                "UNISTR('\\8D28\\91CF\\8FC7\\7A0B'), " +  // 质量过程
                "'/tiros/basic/quality-process', 'tiros/basic/QualityProcess', 'QualityProcess', NULL, 1, NULL, '1', 2, 0, 'bar-chart', 1, 1, 1, 0, " +
                "UNISTR('\\8D28\\91CF\\8FC7\\7A0B\\7BA1\\7406'), " +  // 质量过程管理
                "'admin', SYSDATE, 'admin', SYSDATE, 0, 0, '1', 0)";
            stmt.execute(sql3);
            System.out.println("  - Quality Process inserted");

            // 3. 定额BOM
            String sql4 = "INSERT INTO SYS_PERMISSION (ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, " +
                "REDIRECT, MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON, IS_ROUTE, IS_LEAF, KEEP_ALIVE, " +
                "HIDDEN, DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, RULE_FLAG, STATUS, " +
                "INTERNAL_OR_EXTERNAL) VALUES (" +
                "'tiros_quota_bom', 'tiros_basic_manage_parent', " +
                "UNISTR('\\5B9A\\989DBOM'), " +  // 定额BOM
                "'/tiros/basic/quota-bom', 'tiros/basic/QuotaBom', 'QuotaBom', NULL, 1, NULL, '1', 3, 0, 'database', 1, 1, 1, 0, " +
                "UNISTR('\\5B9A\\989DBOM\\7BA1\\7406'), " +  // 定额BOM管理
                "'admin', SYSDATE, 'admin', SYSDATE, 0, 0, '1', 0)";
            stmt.execute(sql4);
            System.out.println("  - Quota BOM inserted");

            // 4. 标准工艺
            String sql5 = "INSERT INTO SYS_PERMISSION (ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, " +
                "REDIRECT, MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON, IS_ROUTE, IS_LEAF, KEEP_ALIVE, " +
                "HIDDEN, DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, RULE_FLAG, STATUS, " +
                "INTERNAL_OR_EXTERNAL) VALUES (" +
                "'tiros_standard_process', 'tiros_basic_manage_parent', " +
                "UNISTR('\\6807\\51C6\\5DE5\\827A'), " +  // 标准工艺
                "'/tiros/basic/standard-process', 'tiros/basic/StandardProcess', 'StandardProcess', NULL, 1, NULL, '1', 4, 0, 'ordered-list', 1, 1, 1, 0, " +
                "UNISTR('\\6807\\51C6\\5DE5\\827A\\7BA1\\7406'), " +  // 标准工艺管理
                "'admin', SYSDATE, 'admin', SYSDATE, 0, 0, '1', 0)";
            stmt.execute(sql5);
            System.out.println("  - Standard Process inserted");

            // 5. 工艺电子手册
            String sql6 = "INSERT INTO SYS_PERMISSION (ID, PARENT_ID, NAME, URL, COMPONENT, COMPONENT_NAME, " +
                "REDIRECT, MENU_TYPE, PERMS, PERMS_TYPE, SORT_NO, ALWAYS_SHOW, ICON, IS_ROUTE, IS_LEAF, KEEP_ALIVE, " +
                "HIDDEN, DESCRIPTION, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, RULE_FLAG, STATUS, " +
                "INTERNAL_OR_EXTERNAL) VALUES (" +
                "'tiros_tech_manual', 'tiros_basic_manage_parent', " +
                "UNISTR('\\5DE5\\827A\\7535\\5B50\\624B\\518C'), " +  // 工艺电子手册
                "'/tiros/basic/tech-manual', 'tiros/basic/TechManual', 'TechManual', NULL, 1, NULL, '1', 5, 0, 'book', 1, 1, 1, 0, " +
                "UNISTR('\\5DE5\\827A\\7535\\5B50\\624B\\518C\\7BA1\\7406'), " +  // 工艺电子手册管理
                "'admin', SYSDATE, 'admin', SYSDATE, 0, 0, '1', 0)";
            stmt.execute(sql6);
            System.out.println("  - Tech Manual inserted");

            stmt.close();
            conn.commit();
            System.out.println("\nAll menus inserted successfully!");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
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
}
