import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;

public class TestJeecgPassword {
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

    public static void main(String[] args) {
        System.out.println("Testing jeecg user password...");
        String username = "jeecg";
        String salt = "vDDkDzrK";
        String expectedHash = "3dd8371f3cf8240e";
        String[] passwords = {"123456", "jeecg", "jeecg123"};

        for (String pwd : passwords) {
            String encrypted = encrypt(username, pwd, salt);
            boolean match = encrypted != null && encrypted.equals(expectedHash);
            System.out.println("Password: " + pwd + " => " + encrypted + (match ? " MATCH!" : ""));
        }
    }
}
