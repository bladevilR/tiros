import java.sql.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.SecureRandom;

public class ResetAdminPassword {
    public static final String ALGORITHM = "PBEWithMD5AndDES";
    private static final int ITERATIONCOUNT = 1000;

    private static Key getPBEKey(String password) {
        SecretKeyFactory keyFactory;
        SecretKey secretKey = null;
        try {
            keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
            secretKey = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    public static String encrypt(String plaintext, String password, String salt) {
        Key key = getPBEKey(password);
        byte[] encipheredData = null;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt.getBytes(), ITERATIONCOUNT);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
            encipheredData = cipher.doFinal(plaintext.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesToHexString(encipheredData);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            int val = (b & 0xFF);
            if (val < 16) sb.append('0');
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().substring(0, 8);
    }

    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//10.98.14.12:1521/tirostest";
        String dbUser = "tiros_test";
        String dbPassword = "tirostest#123";
        String newPassword = "123456";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
            System.out.println("Connected to database successfully!");

            String selectSql = "SELECT username, salt FROM sys_user WHERE username = 'admin'";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String oldSalt = rs.getString("salt");

                System.out.println("Found admin user");
                System.out.println("Current salt: " + oldSalt);

                String encryptedPassword = encrypt(username, newPassword, oldSalt);
                System.out.println("New encrypted password: " + encryptedPassword);

                String updateSql = "UPDATE sys_user SET password = ? WHERE username = 'admin'";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, encryptedPassword);
                int updated = updateStmt.executeUpdate();

                if (updated > 0) {
                    System.out.println("SUCCESS: Admin password reset to '123456'");
                } else {
                    System.out.println("FAILED: No rows updated");
                }

                updateStmt.close();
            } else {
                System.out.println("Admin user not found in database!");
            }

            rs.close();
            selectStmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }
    }
}
