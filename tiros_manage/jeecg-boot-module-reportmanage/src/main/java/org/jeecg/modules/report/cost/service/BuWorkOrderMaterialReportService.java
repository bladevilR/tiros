package org.jeecg.modules.report.cost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.report.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.report.cost.bean.vo.BuCostDetailTotalVO;
import org.jeecg.modules.report.cost.bean.vo.BuReportCostDetailQueryVO;

/**
 * <p>
 * 工单物料 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
public interface BuWorkOrderMaterialReportService extends IService<BuWorkOrderMaterial> {

    /**
     * 根据条件查询物资消耗明细数据
     *
     * @param queryVO 查询条件
     * @return 物资消耗明细数据
     * @throws Exception 异常信息
     */
    BuCostDetailTotalVO getCostDetailStatistic(BuReportCostDetailQueryVO queryVO) throws Exception;

}
