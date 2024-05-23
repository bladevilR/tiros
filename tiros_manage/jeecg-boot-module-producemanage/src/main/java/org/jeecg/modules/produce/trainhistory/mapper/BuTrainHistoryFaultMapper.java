package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.fault.bean.vo.BuFaultTimeEffectQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryFault;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryFaultQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.HistoryRecordsQueryVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆履历-故障记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryFaultMapper extends BaseMapper<BuTrainHistoryFault> {

    /**
     * 批量插入
     *
     * @param list 故障记录列表
     */
    void insertList(@Param("list") List<BuTrainHistoryFault> list);

    /**
     * 根据条件查询车辆履历故障记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 故障记录分页
     */
    IPage<BuTrainHistoryFault> selectPageByCondition(IPage<BuTrainHistoryFault> page, @Param("queryVO") BuTrainHistoryFaultQueryVO queryVO);

    /**
     * 根据条件查询车辆履历故障记录列表，返回简单信息，用于分析
     *
     * @param queryVO 查询条件
     * @return 故障记录简单信息列表
     */
    List<BuTrainHistoryFault> selectSimpleListByCondition(@Param("queryVO") HistoryRecordsQueryVO queryVO);

    /**
     * 从故障信息查询需归档的故障记录
     *
     * @param date 上次归档时间
     * @return 故障记录列表
     */
    List<BuTrainHistoryFault> selectListNeedCollect(@Param("date") Date date);

    /**
     * 根据id查询需归档的故障记录
     *
     * @param faultIdList 故障id
     * @return 故障记录列表
     */
    List<BuTrainHistoryFault> selectFaultNeedCollectByFaultIdList(@Param("faultIdList") List<String> faultIdList);

    /**
     * 根据时效分析查询条件查询车辆履历中属于检修的故障信息，返回简单信息
     *
     * @param queryVO 时效分析查询条件
     * @return 故障记录简单信息列表
     */
    List<BuTrainHistoryFault> selectMaximoSimpleListByBuFaultTimeEffectQueryVO(@Param("queryVO") BuFaultTimeEffectQueryVO queryVO);

    /**
     * 根据时效分析查询架修周期内的车辆履历中属于架大修的故障信息，返回简单信息
     *
     * @param queryVO 时效分析查询条件，架修周期时间
     * @return 故障数量
     */
    List<BuTrainHistoryFault> selectJdxSimpleListByBuFaultTimeEffectQueryVO(@Param("queryVO") BuFaultTimeEffectQueryVO queryVO);

}
