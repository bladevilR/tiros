package org.jeecg.modules.basemanage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * 启动时自动检测并创建缺失的业务表
 * 直接通过JDBC连接Oracle，不依赖动态数据源
 */
@Slf4j
@Component
public class DatabaseTableInitializer implements CommandLineRunner {

    @Autowired
    private Environment env;

    @Override
    public void run(String... args) {
        Connection conn = null;
        try {
            String url = env.getProperty("spring.datasource.dynamic.datasource.oracle.url");
            String user = env.getProperty("spring.datasource.dynamic.datasource.oracle.username");
            String pass = env.getProperty("spring.datasource.dynamic.datasource.oracle.password");
            String driver = env.getProperty("spring.datasource.dynamic.datasource.oracle.driver-class-name",
                    "oracle.jdbc.driver.OracleDriver");

            if (url == null || user == null) {
                log.warn("未找到Oracle数据源配置，跳过自动建表");
                return;
            }

            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false);

            log.info("====== 开始检测业务表 ======");

            ClassPathResource resource = new ClassPathResource("sql/init_tables_oracle.sql");
            if (!resource.exists()) {
                log.warn("init_tables_oracle.sql 不存在，跳过建表");
                return;
            }

            String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            String[] blocks = sql.split("-- @BLOCK@");

            int created = 0;
            int skipped = 0;

            for (String block : blocks) {
                String trimmed = block.trim();
                if (trimmed.isEmpty()) continue;

                String tableName = extractTableName(trimmed);

                // 菜单块特殊处理
                if (tableName != null && tableName.startsWith("SYS_PERMISSION")) {
                    executeMenuBlock(conn, trimmed);
                    created++;
                    continue;
                }

                if (tableName != null && tableExists(conn, tableName)) {
                    log.info("  表已存在，跳过: {}", tableName);
                    skipped++;
                    continue;
                }

                executeBlock(conn, trimmed);
                if (tableName != null) {
                    log.info("  已创建表: {}", tableName);
                    created++;
                }
            }

            conn.commit();
            log.info("====== 建表检测完成: 新建{}张, 已存在{}张 ======", created, skipped);

        } catch (Exception e) {
            log.error("自动建表异常，不影响启动: {}", e.getMessage(), e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private void executeBlock(Connection conn, String block) {
        String[] statements = block.split(";");
        for (String stmt : statements) {
            String s = stmt.trim();
            if (s.isEmpty() || s.startsWith("--")) continue;
            // 跳过纯注释行
            StringBuilder clean = new StringBuilder();
            for (String line : s.split("\n")) {
                String l = line.trim();
                if (!l.startsWith("--")) clean.append(l).append("\n");
            }
            String finalSql = clean.toString().trim();
            if (finalSql.isEmpty()) continue;
            try (Statement st = conn.createStatement()) {
                st.execute(finalSql);
            } catch (SQLException e) {
                String msg = e.getMessage() != null ? e.getMessage() : "";
                if (msg.contains("ORA-00955") || msg.contains("ORA-01408")
                        || msg.contains("ORA-00001")) {
                    // 对象已存在，忽略
                } else {
                    log.warn("SQL执行异常: {} | SQL: {}", msg,
                            finalSql.length() > 120 ? finalSql.substring(0, 120) + "..." : finalSql);
                }
            }
        }
    }

    private void executeMenuBlock(Connection conn, String block) {
        String[] statements = block.split(";");
        for (String stmt : statements) {
            String s = stmt.trim();
            if (s.isEmpty() || s.startsWith("--")) continue;
            StringBuilder clean = new StringBuilder();
            for (String line : s.split("\n")) {
                String l = line.trim();
                if (!l.startsWith("--")) clean.append(l).append(" ");
            }
            String finalSql = clean.toString().trim();
            if (finalSql.isEmpty() || !finalSql.toUpperCase().startsWith("INSERT")) continue;
            try (Statement st = conn.createStatement()) {
                st.execute(finalSql);
            } catch (SQLException e) {
                // 主键冲突说明已存在，忽略
                String msg = e.getMessage() != null ? e.getMessage() : "";
                if (!msg.contains("ORA-00001")) {
                    log.warn("菜单SQL异常: {}", msg);
                }
            }
        }
    }

    private boolean tableExists(Connection conn, String tableName) {
        String sql = "SELECT COUNT(1) FROM user_tables WHERE table_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private String extractTableName(String block) {
        for (String line : block.split("\n")) {
            String t = line.trim();
            if (t.startsWith("-- TABLE:")) {
                return t.substring("-- TABLE:".length()).trim();
            }
        }
        return null;
    }
}
