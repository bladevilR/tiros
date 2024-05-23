package org.jeecg.modules.quality.measureanalyse.service;

import org.jeecg.modules.quality.measureanalyse.bean.vo.BuMeasureAnalyseQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 测量值分析 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/20
 */
public interface BuMeasureAnalyseService {

    /**
     * 根据条件查询测量项值趋势
     *
     * @param queryVO 查询条件
     * @return 测量项值趋势
     * @throws Exception 异常信息
     */
    List<Map<String, Object>> getMeasureTrend(BuMeasureAnalyseQueryVO queryVO) throws Exception;

}
