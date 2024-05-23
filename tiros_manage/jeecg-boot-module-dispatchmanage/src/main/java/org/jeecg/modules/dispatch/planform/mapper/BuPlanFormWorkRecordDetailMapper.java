package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormWorkRecordDetail;

import java.util.List;

/**
 * <p>
 * 列计划作业记录明细结果 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuPlanFormWorkRecordDetailMapper extends BaseMapper<BuPlanFormWorkRecordDetail> {

    /**
     * 批量插入
     *
     * @param list 作业记录明细实例列表
     */
    void insertList(@Param("list") List<BuPlanFormWorkRecordDetail> list);

}
