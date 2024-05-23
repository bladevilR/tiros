package org.jeecg.modules.board.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;
import org.jeecg.modules.board.cost.bean.vo.BuMaterialAlertVO;

import java.util.List;

/**
 * <p>
 * 工单物料 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-02
 */
public interface BuWorkOrderMaterialBoardMapper extends BaseMapper<BuWorkOrderMaterial> {

    /**
     * 根据年份和条件查询已领用的工单物料
     *
     * @param year    年份
     * @param queryVO 查询条件
     * @return 已领用的工单物料
     */
    List<BuWorkOrderMaterial> selectCostListByYearAndCondition(@Param("year") Integer year, @Param("queryVO") BuBoardCostQueryVO queryVO);

    /**
     * 根据条件查询已领用的工单物料
     *
     * @param queryVO 查询条件
     * @return 已领用的工单物料
     */
    List<BuWorkOrderMaterial> selectCostListByCondition(@Param("queryVO") BuBoardCostQueryVO queryVO);

    /**
     * 查询物料安全库存预警
     *
     * @return 物料安全库存预警
     */
    List<BuMaterialAlertVO> selectMaterialAlertVOList();

}
