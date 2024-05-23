package org.jeecg.common.util;

import java.math.BigDecimal;

/**
 * <p>
 * 百分比工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/19
 */
public class PercentUtils {

    public static Double percent(int divisor, int dividend) {
        return percent(new Integer(divisor).doubleValue(), new Integer(dividend).doubleValue());
    }

    public static Double percent(int divisor, long dividend) {
        return percent(new Integer(divisor).doubleValue(), new Long(dividend).doubleValue());
    }

    public static Double percent(long divisor, int dividend) {
        return percent(new Long(divisor).doubleValue(), new Integer(dividend).doubleValue());
    }

    public static Double percent(long divisor, long dividend) {
        return percent(new Long(divisor).doubleValue(), new Long(dividend).doubleValue());
    }

    public static Double percent(float divisor, float dividend) {
        return percent(new Float(divisor).doubleValue(), new Float(dividend).doubleValue());
    }

    public static Double percent(double divisor, double dividend) {
        return percent(divisor, dividend, 2);
    }

    public static Double percent(double divisor, double dividend, int scale) {
        double percent;
        if (dividend == 0) {
            percent = 100D;
        } else {
            percent = new BigDecimal(divisor * 100 / dividend).setScale(scale, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().doubleValue();
        }
        return new BigDecimal(percent).stripTrailingZeros().doubleValue();
    }


    public static String percentWithSign(int divisor, int dividend) {
        return percentWithSign(new Integer(divisor).doubleValue(), new Integer(dividend).doubleValue());
    }

    public static String percentWithSign(int divisor, long dividend) {
        return percentWithSign(new Integer(divisor).doubleValue(), new Long(dividend).doubleValue());
    }

    public static String percentWithSign(long divisor, int dividend) {
        return percentWithSign(new Long(divisor).doubleValue(), new Integer(dividend).doubleValue());
    }

    public static String percentWithSign(long divisor, long dividend) {
        return percentWithSign(new Long(divisor).doubleValue(), new Long(dividend).doubleValue());
    }

    public static String percentWithSign(float divisor, float dividend) {
        return percentWithSign(new Float(divisor).doubleValue(), new Float(dividend).doubleValue());
    }

    public static String percentWithSign(double divisor, double dividend) {
        return percent(divisor, dividend) + "%";
    }

    public static String percentWithSign(double divisor, double dividend, int scale) {
        return percent(divisor, dividend, scale) + "%";
    }

}
