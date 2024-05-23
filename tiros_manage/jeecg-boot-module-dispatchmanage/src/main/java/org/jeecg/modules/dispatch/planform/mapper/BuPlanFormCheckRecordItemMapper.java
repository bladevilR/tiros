package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormCheckRecordItem;

import java.util.List;

/**
 * <p>
 * 作业检查项明细(实例) Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
public interface BuPlanFormCheckRecordItemMapper extends BaseMapper<BuPlanFormCheckRecordItem> {

    /**
     * 批量插入
     *
     * @param list 作业检查项明细(实例)列表
     */
    void insertList(@Param("list") List<BuPlanFormCheckRecordItem> list);

    /**
     * 根据检查记录表实例id查询检查项明细及整改管理信息
     *
     * @param checkRecordId 检查记录表实例id
     * @return 检查项明细及整改管理信息
     */
    List<BuPlanFormCheckRecordItem> selectListWithRectifyByCheckRecordId(@Param("checkRecordId") String checkRecordId ,@Param("recordIds")String recordIds);

    /**
     * 根据检查记录表实例id查询检查项明细
     *
     * @param checkRecordId 检查记录表实例id
     * @return 检查项明细
     */
    List<BuPlanFormCheckRecordItem> selectListByCheckRecordId(@Param("checkRecordId") String checkRecordId ,@Param("recordIds")String recordIds);

}
