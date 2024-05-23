package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormDataRecord;

/**
 * <p>
 * 列计划表单实列(记录表) Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuPlanFormDataRecordMapper extends BaseMapper<BuPlanFormDataRecord> {

    /**
     * 更新数据记录表实例
     *
     * @param id             id
     * @param structDetailId 车辆结构明细id
     * @param title          标题
     */
    int updateStructAndTitleById(@Param("id") String id, @Param("structDetailId") String structDetailId, @Param("title") String title);

    /**
     * 根据列计划表单实列(记录表)id查询记录表信息
     *
     * @param id 列计划表单实列(记录表)id
     * @return 记录表信息
     */
    BuPlanFormDataRecord selectDataRecordById(@Param("id") String id);

    /**
     * 根据列计划表单实列(记录表)id查询记录表信息及数据值记录
     *
     * @param id 列计划表单实列(记录表)id
     * @return 记录表信息及数据值记录
     */
    BuPlanFormDataRecord selectDataRecordWithValuesById(@Param("id") String id);

}
