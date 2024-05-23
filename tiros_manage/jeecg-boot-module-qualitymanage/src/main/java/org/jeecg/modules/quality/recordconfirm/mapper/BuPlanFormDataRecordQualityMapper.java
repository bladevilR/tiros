package org.jeecg.modules.quality.recordconfirm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormDataRecord;

/**
 * <p>
 * 列计划表单实列(记录表) Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
public interface BuPlanFormDataRecordQualityMapper extends BaseMapper<BuPlanFormDataRecord> {

    /**
     * 根据列计划表单实列(记录表)id查询记录表信息及数据值记录
     *
     * @param id 列计划表单实列(记录表)id
     * @return 记录表信息及数据值记录
     */
    BuPlanFormDataRecord selectDataRecordWithValuesById(@Param("id") String id);

}
