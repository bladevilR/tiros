package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialAssignDetail;
import org.jeecg.modules.third.jdx.bean.bo.ConsumeSyncBO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物料分配明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuMaterialAssignDetailThirdMapper extends BaseMapper<BuMaterialAssignDetail> {

    /**
     * 更新领用单的同步时间
     *
     * @param list 同步返回数据
     */
    void updateApplySyncTime(@Param("list") List<ConsumeSyncBO> list);

    /**
     * 更新领用单的同步返回状态和时间
     *
     * @param list 同步返回数据
     */
    void updateApplySyncResult(@Param("list") List<ConsumeSyncBO> list);

    /**
     * 根据领用明细id查询物料分配明细
     *
     * @param applyDetailId 领用明细id
     * @return 物料分配明细列表
     */
    List<BuMaterialAssignDetail> selectListByApplyDetailId(@Param("applyDetailId") String applyDetailId);

    /**
     * 查询时间点后、所有已消耗的工单物料
     *
     * @param date 时间点
     * @return 所有已消耗的工单物料
     */
    List<BuMaterialAssignDetail> selectMaterialConsumedListByDate(@Param("date") Date date);

    /**
     * 根据分配明细id查询领用单id
     *
     * @param assignDetailIdList 分配明细id列表
     * @return 领用单id列表
     */
    List<String> selectApplyIdListByAssignDetailIdList(@Param("assignDetailIdList") List<String> assignDetailIdList);

    /**
     * 根据领用单id查询领用单下的分配明细id
     *
     * @param applyIdList 领用单id
     * @return 分配明细id
     */
    List<BuMaterialAssignDetail> selectListByApplyIdList(@Param("applyIdList") List<String> applyIdList);

}
