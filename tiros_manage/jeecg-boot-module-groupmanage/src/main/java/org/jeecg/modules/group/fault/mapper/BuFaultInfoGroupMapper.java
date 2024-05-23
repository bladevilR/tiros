package org.jeecg.modules.group.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.group.fault.bean.BuFaultInfo;
import org.jeecg.modules.group.fault.bean.vo.BuFaultInfoQueryVO;

import java.util.List;

/**
 * <p>
 * 故障信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/12
 */
public interface BuFaultInfoGroupMapper extends BaseMapper<BuFaultInfo> {

    /**
     * 根据条件分页查询故障信息
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
    BuFaultInfo selectFaultById(@Param("id") String id);

    /**
     * 根据故障id查询故障处理记录
     *
     * @param id 故障id
     * @return 故障处理记录
     */
    List<BuFaultHandleRecord> selectHandleRecordListByFaultId(@Param("id") String id);

    List<BuFaultInfo> selectListForMaximoByIdList(@Param("faultIdList") List<String> faultIdList);

}
