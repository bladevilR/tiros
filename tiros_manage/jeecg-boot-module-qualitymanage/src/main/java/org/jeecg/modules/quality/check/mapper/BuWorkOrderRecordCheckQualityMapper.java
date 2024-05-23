package org.jeecg.modules.quality.check.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.quality.check.bean.BuWorkOrderRecordCheck;

/**
 * <p>
 * 作业记录明细检查信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
public interface BuWorkOrderRecordCheckQualityMapper extends BaseMapper<BuWorkOrderRecordCheck> {

    /**
     * 如果是专检时，则需要将检查结果填写到明细表
     *
     * @param recordDetailId 所属记录明细id
     * @param recordDetailResult         检查结果  作业记录明细中 结果result（是否异常 0无 1有）
     */
    void writeCheckResultToRecordDetail(String recordDetailId, Integer recordDetailResult);

}
