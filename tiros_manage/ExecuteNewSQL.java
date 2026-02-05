import java.io.*;
import java.sql.*;
import java.util.*;

public class ExecuteNewSQL {
    public static void main(String[] args) {
        // 数据库连接信息（使用测试环境）
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String username = "tiros_test";
        String password = "tirostest#123";

        // 需要执行的SQL文件列表
        String[] sqlFiles = {
            "jeecg-boot-module-basemanage/src/main/resources/sql/bu_base_standard_process.sql",
            "jeecg-boot-module-basemanage/src/main/resources/sql/bu_base_quota_bom.sql",
            "jeecg-boot-module-basemanage/src/main/resources/sql/bu_base_quality_process.sql",
            "jeecg-boot-module-basemanage/src/main/resources/sql/bu_base_production_notice.sql"
        };

        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("正在连接数据库...");
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功！\n");

            stmt = conn.createStatement();
            int totalSuccess = 0;
            int totalError = 0;

            // 执行每个SQL文件
            for (String sqlFile : sqlFiles) {
                System.out.println("========================================");
                System.out.println("正在执行SQL文件: " + sqlFile);
                System.out.println("========================================");

                try {
                    // 读取SQL文件
                    StringBuilder sqlContent = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sqlContent.append(line).append("\n");
                    }
                    reader.close();

                    // 解析SQL语句
                    List<String> sqlStatements = parseSQLStatements(sqlContent.toString());
                    System.out.println("找到 " + sqlStatements.size() + " 条SQL语句\n");

                    int successCount = 0;
                    int errorCount = 0;

                    for (int i = 0; i < sqlStatements.size(); i++) {
                        String sql = sqlStatements.get(i);

                        try {
                            String preview = sql.length() > 60 ? sql.substring(0, 60) + "..." : sql;
                            System.out.println("[" + (i+1) + "/" + sqlStatements.size() + "] 执行: " + preview);
                            stmt.execute(sql);
                            successCount++;
                            totalSuccess++;
                            System.out.println("    [成功]\n");
                        } catch (SQLException e) {
                            errorCount++;
                            totalError++;
                            System.out.println("    [失败] 错误: " + e.getMessage() + "\n");
                        }
                    }

                    System.out.println("--- 文件执行摘要 ---");
                    System.out.println("总语句数: " + sqlStatements.size());
                    System.out.println("成功: " + successCount);
                    System.out.println("失败: " + errorCount);
                    System.out.println();

                } catch (IOException e) {
                    System.err.println("读取文件失败: " + e.getMessage());
                    System.out.println();
                }
            }

            System.out.println("========================================");
            System.out.println("=== 总体执行摘要 ===");
            System.out.println("========================================");
            System.out.println("总成功数: " + totalSuccess);
            System.out.println("总失败数: " + totalError);

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("\n数据库连接已关闭");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> parseSQLStatements(String content) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        String[] lines = content.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();

            // 跳过空行和注释行
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                continue;
            }

            // 添加行到当前语句
            currentStatement.append(line).append("\n");

            // 如果行以分号结束，则语句完成
            if (trimmedLine.endsWith(";")) {
                String sql = currentStatement.toString().trim();
                // 移除末尾分号
                if (sql.endsWith(";")) {
                    sql = sql.substring(0, sql.length() - 1).trim();
                }
                if (!sql.isEmpty()) {
                    statements.add(sql);
                }
                currentStatement = new StringBuilder();
            }
        }

        return statements;
    }
}
