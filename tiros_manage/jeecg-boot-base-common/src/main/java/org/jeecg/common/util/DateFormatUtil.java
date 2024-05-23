package org.jeecg.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>
 * 日期格式化转化工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-14
 */
public class DateFormatUtil {

    private static final Object LOCK_OBJECT = new Object();

    private static final Map<String, ThreadLocal<SimpleDateFormat>> THREAD_LOCAL_MAP = new HashMap<>();

    public static SimpleDateFormat getDateFormat(final String pattern, final String timezone) {
        String key = pattern;
        if (StringUtils.isNotBlank(timezone)) {
            key = pattern + "&" + timezone;
        }
        ThreadLocal<SimpleDateFormat> threadLocal = THREAD_LOCAL_MAP.get(key);

        if (threadLocal == null) {
            synchronized (LOCK_OBJECT) {
                threadLocal = THREAD_LOCAL_MAP.get(pattern);
                if (threadLocal == null) {
                    threadLocal = ThreadLocal.withInitial(() -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                        if (StringUtils.isNotBlank(timezone)) {
                            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
                        }
                        return dateFormat;
                    });

                    THREAD_LOCAL_MAP.put(key, threadLocal);
                }
            }
        }

        return threadLocal.get();
    }

    public static String format(Date date, String pattern) {
        return getDateFormat(pattern, null).format(date);
    }

    public static String format(Date date, String pattern, String timezone) {
        return getDateFormat(pattern, timezone).format(date);
    }

    public static Date parse(String dateString, String pattern) throws ParseException {
        return getDateFormat(pattern, null).parse(dateString);
    }

    public static Date parse(String dateString, String pattern, String timezone) throws ParseException {
        return getDateFormat(pattern, timezone).parse(dateString);
    }

}
