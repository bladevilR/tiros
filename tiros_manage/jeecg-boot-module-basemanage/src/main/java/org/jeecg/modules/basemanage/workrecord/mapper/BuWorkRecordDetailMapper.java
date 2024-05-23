package org.jeecg.modules.basemanage.workrecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordDetail;
import org.jeecg.modules.basemanage.workrecord.bean.vo.BuWorkRecordDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 作业记录表明细, 一条规程项关联一条或多条作业记录明细，如果是多条表示对这条规程项的拆分 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
public interface BuWorkRecordDetailMapper extends BaseMapper<BuWorkRecordDetail> {

    /**
     * 根据条件查询作业记录表明细列表
     *
     * @param queryVO 查询条件
     * @return 作业记录表明细列表
     */
    List<BuWorkRecordDetail> listByCondition(@Param("queryVO") BuWorkRecordDetailQueryVO queryVO);

    /**
     * 根据作业记录表id列表查询作业记录明细id
     *
     * @param workRecordIdList 作业记录表id列表
     * @return 作业记录明细id
     */
    List<String> selectIdListByWorkRecordIdList(@Param("workRecordIdList") List<String> workRecordIdList);

    /**
     * 根据作业记录明细分项id列表查询作业记录明细id
     *
     * @param categoryIdList 作业记录明细分项id列表
     * @return 作业记录明细id
     */
    List<String> selectIdListByCategoryIdList(@Param("categoryIdList") List<String> categoryIdList);
}
