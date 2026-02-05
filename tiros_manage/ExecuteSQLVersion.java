import java.io.*;
import java.sql.*;
import java.util.*;

public class ExecuteSQLVersion {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String username = "tiros_test";
        String password = "tirostest#123";
        String sqlFile = "jeecg-boot-module-basemanage/src/main/resources/sql/tech_manual_version.sql";

        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected successfully!\n");

            System.out.println("Reading SQL file: " + sqlFile);
            StringBuilder sqlContent = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
            String line;
            while ((line = reader.readLine()) != null) {
                sqlContent.append(line).append("\n");
            }
            reader.close();

            List<String> sqlStatements = parseSQLStatements(sqlContent.toString());
            System.out.println("Found " + sqlStatements.size() + " SQL statements\n");

            stmt = conn.createStatement();
            int successCount = 0;
            int errorCount = 0;

            for (int i = 0; i < sqlStatements.size(); i++) {
                String sql = sqlStatements.get(i);

                try {
                    String preview = sql.length() > 60 ? sql.substring(0, 60) + "..." : sql;
                    System.out.println("[" + (i+1) + "/" + sqlStatements.size() + "] Executing: " + preview);
                    stmt.execute(sql);
                    successCount++;
                    System.out.println("    [OK] Success\n");
                } catch (SQLException e) {
                    errorCount++;
                    System.out.println("    [FAIL] Error: " + e.getMessage() + "\n");
                }
            }

            System.out.println("=== Execution Summary ===");
            System.out.println("Total statements: " + sqlStatements.size());
            System.out.println("Successful: " + successCount);
            System.out.println("Failed: " + errorCount);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
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

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                continue;
            }

            currentStatement.append(line).append("\n");

            if (trimmedLine.endsWith(";")) {
                String sql = currentStatement.toString().trim();
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
