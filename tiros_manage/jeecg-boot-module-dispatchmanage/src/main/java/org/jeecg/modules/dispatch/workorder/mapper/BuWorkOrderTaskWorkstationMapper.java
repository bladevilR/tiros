package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskWorkstation;

import java.util.List;

/**
 * <p>
 * 工单工位= 该表 + 该工单任务关联的工位 ， 合并后去重 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderTaskWorkstationMapper extends BaseMapper<BuWorkOrderTaskWorkstation> {

    /**
     * 批量插入
     *
     * @param list 工单工位列表
     */
    void insertList(@Param("list") List<BuWorkOrderTaskWorkstation> list);

    /**
     * 根据工单id列表查询工位及关联表单信息
     *
     * @param orderIdList 工单id列表
     * @return 工位及关联表单信息
     */
    List<BuWorkOrderTaskWorkstation> selectListAndRefFormListByOrderIdList(@Param("orderIdList") List<String> orderIdList);

}
