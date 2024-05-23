package org.jeecg.modules.produce.summary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.summary.bean.BuRepairReguDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 规程明细 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
@Mapper
@Repository("summaryReguDetailMapper")
public interface BuRepairReguDetailMapper extends BaseMapper<BuRepairReguDetail> {
    /**
     * 根据列计划id 查找规程详情
     * @param planId
     * @return
     */
    List<BuRepairReguDetail> selectListByPlanId(@Param("planId") String planId);

    Integer selectPlannedDays(@Param("reguDetailId") List<String> reguDetailIdList, @Param("planId") String planId);

    /**
     * 根据列计划id 查找所有规程详情
     * @param planId  线路id
     * @return
     */
    List<BuRepairReguDetail> selectListAllByPlanId(@Param("planId") String planId);
}
