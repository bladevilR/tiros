package org.jeecg.modules.outsource.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.outsource.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.outsource.fault.bean.BuFaultInfo;
import org.jeecg.modules.outsource.fault.bean.vo.BuFaultInfoQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-19
 */
public interface BuFaultInfoOutsourceMapper extends BaseMapper<BuFaultInfo> {

    /**
     * 根据条件分页查询故障信息（调度只能看到已提交的故障）
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuFaultInfo> selectPageByCondition(IPage<BuFaultInfo> page, @Param("queryVO") BuFaultInfoQueryVO queryVO);

    /**
     * 根据条件查询故障信息列表--仅查询id+status，用于count
     *
     * @param queryVO 查询条件
     * @return 分页结果
     */
    List<BuFaultInfo> selectIdStatusListForCountByCondition(@Param("queryVO") BuFaultInfoQueryVO queryVO);

    /**
     * 根据id查故障信息
     *
     * @param id 故障id
     * @return 故障信息
     */
    BuFaultInfo selectFaultById(@Param("id") String id);

    /**
     * 根据故障id查询故障处理记录
     *
     * @param id 故障id
     * @return 故障处理记录
     */
    List<BuFaultHandleRecord> selectHandleRecordListByFaultId(@Param("id") String id);

    /**
     * 根据条件查询故障简单信息，用于统计数量
     *
     * @param queryVO 查询条件
     * @return 故障简单信息
     */
    List<BuFaultInfo> selectSimpleListByCondition(@Param("queryVO") BuFaultInfoQueryVO queryVO);

}
