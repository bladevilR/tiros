package org.jeecg.modules.third.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 数据源上下文  该类的作用是持有当前线程环境下的数据源，并切换数据源
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-30
 */
public class DynamicDataSourceContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>() {
        /**
         * 将 master 数据源的 key作为默认数据源的 key
         */
        @Override
        protected String initialValue() {
            return DataSourceEnum.jdxdb.getValue();
        }
    };


    /**
     * 数据源的 key集合，用于切换时判断数据源是否存在
     */
    public static List<Object> dataSourceKeys = new ArrayList<>();

    /**
     * 切换数据源
     *
     * @param key 数据源
     */
    public static void setDataSourceKey(String key) {
        contextHolder.set(key);
    }

    /**
     * 获取数据源
     *
     * @return 数据源
     */
    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    /**
     * 重置数据源
     */
    public static void clearDataSourceKey() {
        contextHolder.remove();
    }

    /**
     * 判断是否包含数据源
     *
     * @param key 数据源key
     * @return 是否包含数据源
     */
    public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }

    /**
     * 添加数据源keys
     *
     * @param keys 数据源keys
     */
    public static void addDataSourceKeys(Collection<?> keys) {
        dataSourceKeys.addAll(keys);
    }

}
