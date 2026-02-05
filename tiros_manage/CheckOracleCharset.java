import java.sql.*;

public class CheckOracleCharset {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String username = "tiros_test";
        String password = "tirostest#123";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, username, password);

            // Check database character set
            System.out.println("=== Database Character Set ===");
            String charsetSql = "SELECT * FROM NLS_DATABASE_PARAMETERS WHERE PARAMETER IN ('NLS_CHARACTERSET', 'NLS_NCHAR_CHARACTERSET')";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(charsetSql);
            while (rs.next()) {
                System.out.println(rs.getString("PARAMETER") + " = " + rs.getString("VALUE"));
            }
            rs.close();

            // Check NAME column data type
            System.out.println("\n=== SYS_PERMISSION.NAME Column Info ===");
            String colSql = "SELECT DATA_TYPE, DATA_LENGTH, CHAR_LENGTH FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'SYS_PERMISSION' AND COLUMN_NAME = 'NAME'";
            rs = stmt.executeQuery(colSql);
            if (rs.next()) {
                System.out.println("DATA_TYPE: " + rs.getString("DATA_TYPE"));
                System.out.println("DATA_LENGTH: " + rs.getInt("DATA_LENGTH"));
                System.out.println("CHAR_LENGTH: " + rs.getInt("CHAR_LENGTH"));
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
