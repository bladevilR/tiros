package org.jeecg.modules.basemanage.workrecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordCategory;

import java.util.List;

/**
 * <p>
 * 作业记录明细分项 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-08
 */
public interface BuWorkRecordCategoryMapper extends BaseMapper<BuWorkRecordCategory> {

    /**
     * 根据作业记录表id查询作业记录明细分项
     *
     * @param workRecordId 作业记录表id
     * @return 作业记录明细分项列表
     */
    List<BuWorkRecordCategory> selectListByWorkRecordId(@Param("workRecordId") String workRecordId);

    /**
     * 根据作业记录表id列表查询作业记录明细分项id
     *
     * @param workRecordIdList 作业记录表id列表
     * @return 作业记录明细分项id
     */
    List<String> selectIdListByWorkRecordIdList(@Param("workRecordIdList") List<String> workRecordIdList);
}
