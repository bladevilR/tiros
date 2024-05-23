package org.jeecg.modules.third.maximo.mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * maximo自定义 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-07-30
 */
public interface CustomMaximoMapper {

    List<Map<String, Object>> selectBlockingErrorInQueueList();

}
