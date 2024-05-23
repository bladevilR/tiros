package org.jeecg.common.tiros.util;

import java.math.BigDecimal;

/**
 * <p>
 * 比较相同 工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-14
 */
public class SameUtil {

    public static boolean same(String var1, String var2) {
        return same(var1, var2, false);
    }

    public static boolean same(BigDecimal var1, BigDecimal var2) {
        return same(var1, var2, false);
    }

    public static boolean same(Integer var1, Integer var2) {
        return same(var1, var2, false);
    }

    public static boolean same(String var1, String var2, boolean emptyEqualsNull) {
        boolean var1Null = null == var1;
        boolean var2Null = null == var2;

        if (var1Null) {
            if (emptyEqualsNull) {
                return var2Null || var2.trim().equals("");
            } else {
                return var2Null;
            }
        } else {
            return !var2Null && var2.trim().equals(var1.trim());
        }
    }

    public static boolean same(BigDecimal var1, BigDecimal var2, boolean zeroEqualsNull) {
        boolean var1Null = null == var1;
        boolean var2Null = null == var2;

        if (var1Null) {
            if (zeroEqualsNull) {
                return var2Null || BigDecimal.ZERO.compareTo(var2) == 0;
            } else {
                return var2Null;
            }
        } else {
            return !var2Null && var1.compareTo(var2) == 0;
        }
    }

    public static boolean same(Integer var1, Integer var2, boolean zeroEqualsNull) {
        boolean var1Null = null == var1;
        boolean var2Null = null == var2;

        if (var1Null) {
            if (zeroEqualsNull) {
                return var2Null || var2 == 0;
            } else {
                return var2Null;
            }
        } else {
            return !var2Null && var1.equals(var2);
        }
    }

}
