package org.jeecg.modules.report.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.report.cost.bean.vo.BuReportCostDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 工单物料 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-17
 */
public interface BuWorkOrderMaterialReportMapper extends BaseMapper<BuWorkOrderMaterial> {

    /**
     * 根据条件查询工单物料
     *
     * @param queryVO 查询条件
     * @return 工单物料
     */
    List<BuWorkOrderMaterial> selectListByCondition(@Param("queryVO") BuReportCostDetailQueryVO queryVO);

}
