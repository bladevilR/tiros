package org.jeecg.common.threadlocal;

/**
 * <p>
 * token存储
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
public class ThreadLocalToken {

    private ThreadLocalToken() {
    }

    private static final ThreadLocal<String> CURRENT_TOKEN_THREAD_LOCAL = new ThreadLocal<>();

    public static void clear() {
        CURRENT_TOKEN_THREAD_LOCAL.remove();
    }

    public static void set(String token) {
        CURRENT_TOKEN_THREAD_LOCAL.set(token);
    }

    public static String getCurrentToken() {
        return CURRENT_TOKEN_THREAD_LOCAL.get();
    }

}
