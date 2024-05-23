package org.jeecg.modules.board.quality.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.modules.board.quality.bean.BuRptBoardTrainFault;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;
import org.jeecg.modules.board.quality.bean.vo.BuCenterQualityFaultProgramVO;

import java.util.List;

/**
 * <p>
 * 故障统计-中间表-车辆维度 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
public interface BuRptBoardTrainFaultService extends IService<BuRptBoardTrainFault> {

    /**
     * 根据条件查询车辆段故障统计列表
     *
     * @param queryVO 查询条件
     * @return 车辆段故障统计列表
     * @throws Exception 异常信息
     */
    List<BuCenterQualityFaultProgramVO> listDepotFault(BuBoardQualityQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询质保期故障趋势
     *
     * @param queryVO 查询条件
     * @return 质保期故障趋势
     * @throws Exception 异常信息
     */
    List<LineChartVO> listWarrantyPeriodFaultTrend(BuBoardQualityQueryVO queryVO) throws Exception;

}
