package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.*;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuMaximoTrainAssetChildrenQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuWorkLeaveRecordQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuWorkRectifyQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
public interface BuTrainInfoProduceMapper extends BaseMapper<BuTrainInfo> {

    /**
     * 根据车辆号查询车辆信息
     *
     * @param trainNo 车辆号
     * @return 车辆信息
     */
    BuTrainInfo selectTrainInfoByTrainNo(@Param("trainNo") String trainNo);

    /**
     * 查询所有车辆信息
     *
     * @return 所有车辆信息
     */
    List<BuTrainInfo> selectAll();

    /**
     * 查询有股道信息的车辆列表
     *
     * @param lineId 线路id
     * @return 有股道信息的车辆列表
     */
    List<BuTrainInfo> selectListHasTrack(@Param("lineId") String lineId);

    /**
     * 根据车辆号查询车辆架修周期记录
     *
     * @param trainNo 车辆号
     * @return 车辆架修周期记录列表
     */
    List<BuBaseTrainRepairPeriod> selectTrainRepairPeriodListByTrainNo(@Param("trainNo") String trainNo);

    /**
     * 根据列计划车辆查询车辆结构id
     * @param planId 列计划id
     * @return 车辆结构id
     */
    String selectTrainStructIdByPlanId(@Param("planId") String planId);

    IPage<BuWorkLeaveRecord> pageLeaveRecord(Page<BuWorkLeaveRecord> page, @Param("queryVO") BuWorkLeaveRecordQueryVO queryVO);

    IPage<BuWorkRectify> pageRectify(Page<BuWorkRectify> page, @Param("queryVO") BuWorkRectifyQueryVO queryVO);

    /**
     * 查询资产设备子节点
     *
     * @param queryVO 查询条件
     * @return 子节点列表
     */
    List<BuMaximoTrainAsset> selectChildrenByQueryVO(@Param("queryVO")BuMaximoTrainAssetChildrenQueryVO queryVO);

    /**
     * 查找Maximo设备位置
     * @param locationCodeBatchSub
     * @return
     */
    List<BuMaximoLocation> selectMaximoLocationList(@Param("codeList") List<String> locationCodeBatchSub);
    /**
     * 根据资产设备id列表查询扩展信息
     *
     * @param assetIdBatchSub 资产设备id列表
     * @return 扩展信息列表
     */
    List<BuMaximoTrainAssetExt> selectListByAssetIdList(@Param("assetIdList") List<String> assetIdBatchSub);
}
