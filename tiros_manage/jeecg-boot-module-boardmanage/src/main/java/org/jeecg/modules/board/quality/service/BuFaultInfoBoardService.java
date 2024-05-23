package org.jeecg.modules.board.quality.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.bean.vo.chart.SingleBarChartVO;
import org.jeecg.modules.board.quality.bean.BuFaultInfo;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;
import org.jeecg.modules.board.quality.bean.vo.BuGroupFaultRankingVO;
import org.jeecg.modules.board.quality.bean.vo.BuLatestFaultVO;

import java.util.List;

/**
 * <p>
 * 故障信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-17
 */
public interface BuFaultInfoBoardService extends IService<BuFaultInfo> {

    /**
     * 根据条件查询最新故障信息
     *
     * @param queryVO 查询条件
     * @return 最新故障信息
     * @throws Exception 异常信息
     */
    BuLatestFaultVO getLatestFaultInfo(BuBoardQualityQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询工班发现故障排名
     *
     * @param queryVO 查询条件
     * @return 工班发现故障排名
     * @throws Exception 异常信息
     */
    List<BuGroupFaultRankingVO> getGroupFaultRanking(BuBoardQualityQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询最新委外故障信息
     *
     * @param queryVO 查询条件
     * @return 最新委外故障信息
     * @throws Exception 异常信息
     */
    BuLatestFaultVO getLatestOutsourceFaultInfo(BuBoardQualityQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询故障数量系统分布
     *
     * @param queryVO 查询条件
     * @return 故障数量系统分布
     * @throws Exception 异常信息
     */
    List<PieChartVO> getFaultSystemCount(BuBoardQualityQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询故障数量趋势(按天)
     *
     * @param queryVO 查询条件
     * @return 故障数量趋势(按天)
     * @throws Exception 异常信息
     */
    List<SingleBarChartVO> getDayFaultTrend(BuBoardQualityQueryVO queryVO) throws Exception;

    /**
     * 获取最新故障信息
     *
     * @param num 故障条数
     * @return 最新故障信息结果
     * @throws Exception 异常信息
     */
    List<BuFaultInfo> listLatestFault(Integer num) throws Exception;

    /**
     * 根据id查询故障详细信息
     *
     * @param id 故障id
     * @return 故障详细信息
     * @throws Exception 异常信息
     */
    BuFaultInfo getFaultInfoById(String id) throws Exception;

}
