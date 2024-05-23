package org.jeecg.modules.board.cost.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.cost.bean.*;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 成本自定义查询 mapper接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-02
 */
public interface BuCostCustomMapper {

    /**
     * 统计条件下的工班所有人员
     *
     * @param queryVO 查询条件
     * @return 人员数量
     */
    Integer countGroupUser(@Param("queryVO") BuBoardCostQueryVO queryVO);

    /**
     * 查询指定条件的工单任务填报工时
     *
     * @param queryVO 查询条件
     * @return 工时
     */
    Integer sumFinishOrderTaskUseTime(@Param("queryVO") BuBoardCostQueryVO queryVO);

    /**
     * 查询指定条件的工装id列表
     *
     * @param queryVO 查询条件
     * @return 工装id列表
     */
    List<String> selectWorkshopTool5IdList(@Param("queryVO") BuBoardCostQueryVO queryVO);

    /**
     * 查询指定工装id列表的时间段内工装运用计划
     *
     * @param toolIdList 工装id列表
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return 工装运用计划列表
     */
    List<BuToolPlan> selectToolUserPlanList(@Param("toolIdList") List<String> toolIdList, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 查询指定条件的委外合同支付记录金额合计
     *
     * @param queryVO 查询条件
     * @return 委外合同支付记录金额合计
     */
    BigDecimal sumOutsourcePayAmount(@Param("queryVO") BuBoardCostQueryVO queryVO);

    /**
     * 根据年份和条件查询委外支付
     *
     * @param year    年份
     * @param queryVO 查询条件
     * @return 委外支付
     */
    List<BuContractPay> selectContractPayListByYearAndCondition(@Param("year") Integer year, @Param("queryVO") BuBoardCostQueryVO queryVO);

    /**
     * 查询指定条件的委外出入库明细中部件
     *
     * @param queryVO 查询条件
     * @return 委外出入库明细中部件数量合计
     */
    List<OutsourceAssetPrice>  sumOutsourceAsset(@Param("queryVO") BuBoardCostQueryVO queryVO);

    /**
     * 查询所有修程
     *
     * @return 修程列表
     */
    List<BuRepairProgram> selectAllRepairProgram();
    /**
     * 查询指定条件的特种设备id列表
     *
     * @param queryVO 查询条件
     * @return 特种设备id列表
     */
    List<String> selectWorkshopSpeAssetIdList(@Param("queryVO")BuBoardCostQueryVO queryVO);
    /**
     * 查询指定特种设备id列表的时间段内特种设运用计划
     *
     * @param speAssetIdList 特种设id列表
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return 特种设运用计划列表
     */
    List<BuSpecAssetPlan> selectSpeAssetUserPlanList(@Param("speAssetIdList")List<String> speAssetIdList, @Param("startDate")Date startDate, @Param("endDate")Date endDate);
}
