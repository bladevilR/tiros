package org.jeecg.modules.report.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.cost.bean.BuRptBoardSysMaterial;
import org.jeecg.modules.report.cost.bean.vo.BuReportCostSingleTrainQueryVO;

import java.util.List;

/**
 * <p>
 * 物料成本统计 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-07
 */
public interface BuRptBoardSysMaterialMapper extends BaseMapper<BuRptBoardSysMaterial> {

    /**
     * 批量插入
     * @param sysMaterialList  成本
     */
    void insertBatch(@Param("list")List<BuRptBoardSysMaterial> sysMaterialList);

    /**
     * 查询车辆的车厢数量
     *
     * @param lineId  线路id
     * @param trainNo 车号
     * @return 车辆的车厢数量
     */
    Integer selectTrainCarsNumberByLineIdAndTrainNo(@Param("lineId") String lineId, @Param("trainNo") String trainNo);

    /**
     * 根据年份和条件查询物资成本统计
     *
     * @param queryVO 查询条件
     * @return 物资成本统计
     */
    List<BuRptBoardSysMaterial> selectListByCondition(@Param("queryVO") BuReportCostSingleTrainQueryVO queryVO);

}
