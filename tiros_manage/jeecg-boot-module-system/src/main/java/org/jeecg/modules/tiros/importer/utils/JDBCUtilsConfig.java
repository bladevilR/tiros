package org.jeecg.modules.tiros.importer.utils;


import com.google.api.client.util.Value;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author yyg
 */
public class JDBCUtilsConfig {
    private static Connection con;


    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name}")
    private static String driverClass;
    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private static String url;
    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private static String username;
    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private static String password;

    static {
        try {
          //  readConfig();
            Class.forName(driverClass);
            con = DriverManager.getConnection(url, username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("数据库连接失败");
        }

    }

    public static void readConfig() throws Exception {
        InputStream in = JDBCUtilsConfig.class.getClass().getResourceAsStream("/config/db.properties");
        Properties pro = new Properties();
        pro.load(in);
        driverClass = pro.getProperty("driverClass");
        url = pro.getProperty("url");
        username = pro.getProperty("username");
        password = pro.getProperty("password");
    }

    public static Connection getConnection() {
        return con;
    }

    public static void closeConnection() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}
