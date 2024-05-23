package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderRecordCheck;

/**
 * <p>
 * 如果是专检时，则需要将检查结果填写到明细表 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-12
 */
public interface BuWorkOrderRecordCheckMapper extends BaseMapper<BuWorkOrderRecordCheck> {

}
