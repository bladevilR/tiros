package org.jeecg.modules.board.quality.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.bean.vo.chart.SingleBarChartVO;
import org.jeecg.modules.board.quality.bean.BuRptBoardSysFault;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;

import java.util.List;

/**
 * <p>
 * 故障统计-中间表-系统维度 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
public interface BuRptBoardSysFaultService extends IService<BuRptBoardSysFault> {

    /**
     * 根据条件查询质保期故障各系统分布
     *
     * @param queryVO 查询条件
     * @return 质保期故障各系统分布
     * @throws Exception 异常信息
     */
    List<SingleBarChartVO> listWarrantyPeriodSysFault(BuBoardQualityQueryVO queryVO) throws Exception;

}
