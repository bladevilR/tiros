package org.jeecg.modules.dispatch.fault.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.fault.bean.BuFaultHandleRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultHandleRecordMapper extends BaseMapper<BuFaultHandleRecord> {

    /**
     * 批量插入
     *
     * @param list 故障信息列表
     */
    void insertList(@Param("list") List<BuFaultHandleRecord> list);

}
