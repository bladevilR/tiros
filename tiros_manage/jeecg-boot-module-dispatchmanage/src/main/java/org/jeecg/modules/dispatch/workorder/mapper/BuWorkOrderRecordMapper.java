package org.jeecg.modules.dispatch.workorder.mapper;

import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 作业记录表，1 这里只存放作业记录表的填写信息，其他表单不需要保存到这里，2 如果在计划中是唯一的，则不关联任务， 如果 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderRecordMapper extends BaseMapper<BuWorkOrderRecord> {

}
