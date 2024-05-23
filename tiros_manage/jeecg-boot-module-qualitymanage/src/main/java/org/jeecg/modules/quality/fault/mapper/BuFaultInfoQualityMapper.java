package org.jeecg.modules.quality.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.quality.fault.bean.BuFaultInfo;
import org.jeecg.modules.quality.fault.bean.vo.BuFaultInfoQueryVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 故障信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultInfoQualityMapper extends BaseMapper<BuFaultInfo> {

    /**
     * 根据条件分页查询故障信息（调度只能看到已提交的故障）
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuFaultInfo> selectPageByCondition(IPage<BuFaultInfo> page, @Param("queryVO") BuFaultInfoQueryVO queryVO);

    /**
     * 根据id查故障信息
     *
     * @param id 故障id
     * @return 故障信息
     */
    BuFaultInfo selectFaultById(String id);

    /**
     * 根据故障id查询故障处理记录
     *
     * @param id 故障id
     * @return 故障处理记录
     */
    List<BuFaultHandleRecord> selectHandleRecordListByFaultId(String id);

}
