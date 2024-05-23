package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskFormInst;

import java.util.List;

/**
 * <p>
 * 表单实例和工单任务关联 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-04
 */
public interface BuWorkOrderTaskFormInstMapper extends BaseMapper<BuWorkOrderTaskFormInst> {

    /**
     * 批量插入
     *
     * @param list 表单实例和工单任务关联列表
     */
    void insertList(@Param("list") List<BuWorkOrderTaskFormInst> list);

    /**
     * 查找表单名称
     *
     * @param fromId   实例id
     * @param formType 表单类型
     */
    String selectFormName(@Param("fromId") String fromId, @Param("formType") Integer formType);

}
