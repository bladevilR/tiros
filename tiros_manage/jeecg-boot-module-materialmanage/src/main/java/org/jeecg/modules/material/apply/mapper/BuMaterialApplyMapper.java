package org.jeecg.modules.material.apply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.apply.bean.BuMaterialApply;
import org.jeecg.modules.material.apply.bean.vo.AppApplyOrderQueryVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialApplyQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 物料申请(领用) Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
public interface BuMaterialApplyMapper extends BaseMapper<BuMaterialApply> {

    /**
     * 批量更新领料确认属性
     *
     * @param list 领用单列表
     */
    void updateListForReceive(@Param("list") List<BuMaterialApply> list);

    /**
     * 批量更新领料状态
     *
     * @param list 领用单列表
     */
    void updateListStatus(@Param("list") List<BuMaterialApply> list);

    /**
     * 根据条件分页获取物料领用记录id
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<String> selectApplyIdPageByCondition(IPage<String> page, @Param("queryVO") BuMaterialApplyQueryVO queryVO);

    /**
     * 根据领用单id列表获取物料领用单
     *
     * @param applyIdList 领用单id列表
     * @return 物料领用单列表
     */
    List<BuMaterialApply> selectListByApplyIdList(@Param("applyIdList") List<String> applyIdList);

    /**
     * 根据条件分页获取物料领用记录id
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<String> selectOrderIdPageForAppByCondition(IPage<String> page, @Param("queryVO") AppApplyOrderQueryVO queryVO);

    /**
     * 查询列计划下的关闭工单的领用单和领用明细和状态
     *
     * @param planId 列计划id
     * @return 领用单和领用明细和状态
     */
    List<Map<String, Object>> selectApplyAndDetailStatusListOfCloseOrder(@Param("planId") String planId);

    String selectDepartIdByOrgCode(@Param("orgCode") String orgCode);

}
