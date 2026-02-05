package org.jeecg.modules.third.utils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

/**
 * <p>
 * 数据库查询数据类型转换工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-18
 */
public class DataTypeCastUtil {

    public static boolean isString(Object obj) {
        return obj instanceof String;
    }

    public static boolean isNumber(Object obj) {
        if (obj instanceof BigDecimal) {
            return true;
        }
        if (obj instanceof Integer) {
            return true;
        }
        if (obj instanceof Long) {
            return true;
        }
        if (obj instanceof Double) {
            return true;
        }
        if (obj instanceof Short) {
            return true;
        }
        if (obj instanceof Float) {
            return true;
        }
        if (obj instanceof Byte) {
            return true;
        }

        return false;
    }

    public static boolean isDate(Object obj) {
        // Oracle-specific types support (optional, requires ojdbc driver)
        // if (obj instanceof oracle.sql.TIMESTAMP) { return true; }
        // if (obj instanceof oracle.sql.DATE) { return true; }

        if (obj instanceof java.sql.Date) {
            return true;
        }
        if (obj instanceof java.sql.Timestamp) {
            return true;
        }
        if (obj instanceof Date) {
            return true;
        }

        return false;
    }

    public static String transString(Object obj) {
        if (null == obj) {
            return null;
        }
        if (!isString(obj)) {
            throw new RuntimeException("对象不是String类型");
        }
        return (String) obj;
    }

    public static BigDecimal transNumber(Object obj) {
        if (null == obj) {
            return null;
        }
        if (!isNumber(obj)) {
            throw new RuntimeException("对象不是已知数字类型");
        }

        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        if (obj instanceof Integer) {
            return BigDecimal.valueOf((Integer) obj);
        }
        if (obj instanceof Long) {
            return BigDecimal.valueOf((Long) obj);
        }
        if (obj instanceof Double) {
            return BigDecimal.valueOf((Double) obj);
        }
        if (obj instanceof Short) {
            return BigDecimal.valueOf((Short) obj);
        }
        if (obj instanceof Float) {
            return BigDecimal.valueOf((Float) obj);
        }
        if (obj instanceof Byte) {
            return BigDecimal.valueOf((Byte) obj);
        }

        throw new RuntimeException("对象不是已知数字类型");
    }

    public static Date transDate(Object obj) {
        if (null == obj) {
            return null;
        }
        if (!isDate(obj)) {
            throw new RuntimeException("对象不是已知日期类型");
        }

        // Oracle-specific types support (optional, requires ojdbc driver)
        /*
        if (obj instanceof oracle.sql.TIMESTAMP) {
            try {
                return new Date(((oracle.sql.TIMESTAMP) obj).timestampValue().getTime());
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException("oracle.sql.TIMESTAMP 转换成 java.sql.Timestamp 异常");
            }
        }
        if (obj instanceof oracle.sql.DATE) {
            return new Date(((oracle.sql.DATE) obj).timestampValue().getTime());
        }
        */

        if (obj instanceof java.sql.Date) {
            return new Date(((java.sql.Date) obj).getTime());
        }
        if (obj instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) obj).getTime());
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }

        throw new RuntimeException("对象不是已知日期类型");
    }

}
