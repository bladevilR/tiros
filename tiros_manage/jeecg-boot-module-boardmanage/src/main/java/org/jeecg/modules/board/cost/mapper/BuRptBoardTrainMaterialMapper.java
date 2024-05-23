package org.jeecg.modules.board.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.cost.bean.BuRptBoardTrainMaterial;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;

import java.util.List;

/**
 * <p>
 * 物料成本统计 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
public interface BuRptBoardTrainMaterialMapper extends BaseMapper<BuRptBoardTrainMaterial> {

    /**
     * 根据年份和条件查询物资成本统计
     *
     * @param year    年份
     * @param queryVO 查询条件
     * @return 物资成本统计
     */
    List<BuRptBoardTrainMaterial> listByYearAndCondition(@Param("year") Integer year, @Param("queryVO") BuBoardCostQueryVO queryVO);

}
