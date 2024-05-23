package org.jeecg.modules.board.homepage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.board.homepage.bean.BuRptBoardDataItem;
import org.jeecg.modules.board.homepage.bean.bo.SysDictItemBO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 统计数据项表（首页） Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
public interface BuRptBoardDataItemMapper extends BaseMapper<BuRptBoardDataItem> {

    /**
     * 根据所属数据分类id获取统计数据项
     *
     * @param categoryId 数据分类id
     * @return 统计数据项
     */
    List<BuRptBoardDataItem> selectListByCategoryId(@Param("categoryId") String categoryId);

    /**
     * 根据字典code获取字典项（看板首页数据区颜色/看板首页预警区颜色）
     *
     * @param dictCode 字典code
     * @return 字典项（颜色配置）
     */
    List<SysDictItemBO> selectColorDictItemListByDictCode(@Param("dictCode") String dictCode);

    /**
     * 统计预警表中各预警类型的未处理未忽略的数量
     *
     * @return 各预警类型的未处理未忽略的数量
     */
    List<Map<String, Object>> countAlertTypeAndValidNumber();

    /**
     * 查询当前架修数
     *
     * @return 当前架修数
     */
    Integer countRepairingPlanCurrentMonth();

    /**
     * 查询当前工单数
     *
     * @return 当前工单数
     */
    Integer countUnfinishedOrderCurrentMonth();

    /**
     * 查询当前月所有指定状态的工单
     * @param  status
     * @return
     */
    Integer countCurrentMonthOrderByStatus(@Param("status") Integer status);

    /**
     * 查询作业任务数
     *
     * @return 作业任务数
     */
    Integer countUnfinishedOrderTaskCurrentMonth();

    /**
     * 查询当前工单任务数
     *
     * @return 当前工单任务数
     */
    Integer countUnfinishedOrderAllTaskCurrentMonth();

    /**
     * 查询消耗物料成本
     *
     * @return 消耗物料成本
     */
    List<BuWorkOrderMaterial> countMaterialCostCurrentMonth();

    /**
     * 查询发现故障数
     *
     * @return 发现故障数
     */
    Integer countFaultCurrentMonth();

}
