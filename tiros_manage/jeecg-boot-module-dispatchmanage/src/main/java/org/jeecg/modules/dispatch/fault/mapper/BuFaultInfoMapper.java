package org.jeecg.modules.dispatch.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfo;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultInfoQueryVO;

import java.util.List;

/**
 * <p>
 * 故障信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultInfoMapper extends BaseMapper<BuFaultInfo> {

    /**
     * 批量插入
     *
     * @param list 故障信息列表
     */
    void insertList(@Param("list") List<BuFaultInfo> list);

    /**
     * 设置故障的处理工单id为空
     *
     * @param faultIdList 故障id列表
     */
    void updateHandleOrderIdNull(@Param("faultIdList") List<String> faultIdList);

    /**
     * 更新故障的关闭信息
     *
     * @param list 故障列表
     */
    void updateListCloseInfo(@Param("list") List<BuFaultInfo> list);

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
    BuFaultInfo selectFaultById(@Param("id") String id);

    /**
     * 根据id查故障信息，用于故障统计
     *
     * @param id 故障id
     * @return 故障信息
     */
    BuFaultInfo selectFaultForRptById(@Param("id") String id);

    /**
     * 根据所有已提交的故障信息，用于故障统计
     *
     * @param trainNoList 车号
     * @return 故障信息
     */
    List<BuFaultInfo> selectAllFaultForRpt(@Param("trainNoList") List<String> trainNoList);

    /**
     * 根据故障id查询故障处理记录
     *
     * @param id 故障id
     * @return 故障处理记录
     */
    List<BuFaultHandleRecord> selectHandleRecordListByFaultId(@Param("id") String id);

    List<BuFaultInfo> selectListForMaximoByIdList(@Param("faultIdList") List<String> faultIdList);

}
