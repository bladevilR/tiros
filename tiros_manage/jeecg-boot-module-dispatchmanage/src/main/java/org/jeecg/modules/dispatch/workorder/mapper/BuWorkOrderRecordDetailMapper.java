package org.jeecg.modules.dispatch.workorder.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderRecordDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-12
 */
public interface BuWorkOrderRecordDetailMapper extends BaseMapper<BuWorkOrderRecordDetail> {

    /**
     * 根据工单作业记录id查询作业记录明细
     *
     * @param recordId 工单作业记录id
     * @return 作业记录明细
     */
    List<BuWorkOrderRecordDetail> selectListByOrderRecordId(@Param("recordId") String recordId);

}
