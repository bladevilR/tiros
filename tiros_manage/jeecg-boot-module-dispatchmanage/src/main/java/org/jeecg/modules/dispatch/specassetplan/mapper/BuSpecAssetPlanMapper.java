package org.jeecg.modules.dispatch.specassetplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.specassetplan.bean.BuSpecAssetPlan;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanCheckVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanQueryVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 特种设备运用/保养计划 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-03
 */
public interface BuSpecAssetPlanMapper extends BaseMapper<BuSpecAssetPlan> {

    /**
     * 批量插入
     *
     * @param list 特种设备运用/保养计划列表
     */
    void insertList(@Param("list") List<BuSpecAssetPlan> list);

    /**
     * 根据条件分页查询特种设备运用/保养计划
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuSpecAssetPlan> selectPageByCondition(IPage<BuSpecAssetPlan> page, @Param("queryVO") BuSpecAssetPlanQueryVO queryVO);

    /**
     * 根据id查询特种设备运用/保养计划
     *
     * @param id 特种设备运用/保养计划id
     * @return 特种设备运用/保养计划
     */
    BuSpecAssetPlan selectSpecAssetPlanById(@Param("id") String id);

    /**
     * 查询冲突的特种设备运用/保养计划
     *
     * @param checkVO 查询条件
     * @return 查询冲突结果
     */
    List<BuSpecAssetPlan> selectConflictToolPlanList(@Param("checkVO") BuSpecAssetPlanCheckVO checkVO);

    /**
     * 查询特种设备的运用/保养计划列表
     *
     * @param specAssetId 特种设备id
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @return 运用/保养计划列表
     */
    List<BuSpecAssetPlan> selectListBySpecAssetIdAndTime(@Param("specAssetId") String specAssetId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
