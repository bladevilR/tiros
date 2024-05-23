package org.jeecg.common.tiros.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 数据库操作 批量截取 工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-25
 */
public class DatabaseBatchSubUtil {
    /**
     * oracle数据库允许的批量操作（如in (xxx,xxx,...)）中的数据大小，最大支持1000，稳妥起见，这里设置为800
     */
    public static final int ORACLE_ALLOW_IN_BATCH_SIZE = 800;

    public static <T> List<List<T>> batchSubList(List<T> list) {
        return batchSubList(list, ORACLE_ALLOW_IN_BATCH_SIZE);
    }

    public static <T> List<List<T>> batchSubList(List<T> list, int batchSize) {
        List<List<T>> resultList = new ArrayList<>();

        if (list.size() > batchSize) {
            int size = list.size();
            int cycleIndex = size / batchSize + 1;
            for (int i = 0; i < cycleIndex; i++) {
                int fromIndex = i * batchSize;
                int toIndex = fromIndex + batchSize;
                List<T> subList = list.subList(fromIndex, Math.min(toIndex, size));
                resultList.add(subList);
            }
        } else {
            resultList.add(list);
        }

        resultList.removeIf(CollectionUtils::isEmpty);
        return resultList;
    }

}
