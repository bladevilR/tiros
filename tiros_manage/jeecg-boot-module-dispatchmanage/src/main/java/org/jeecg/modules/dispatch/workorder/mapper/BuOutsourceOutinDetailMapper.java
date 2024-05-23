package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceOutinDetail;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 委外出入库明细 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-14
 */
public interface BuOutsourceOutinDetailMapper extends BaseMapper<BuOutsourceOutinDetail> {

    /**
     * 根据车辆号和设备类型id查询该车辆、该设备类型、的出库明细
     *
     * @param trainNo     车辆号
     * @param assetTypeId 设备类型id
     * @return 该车辆、该设备类型、的出库明细
     */
    List<BuOutsourceOutinDetail> selectOutDetailListByTrainNoAndAssetTypeId(@Param("trainNo") String trainNo, @Param("assetTypeId") String assetTypeId, @Param("assetName") String assetName);

    /**
     * 根据车辆号和设备类型id查询该车辆、该设备类型、的入库明细
     *
     * @param trainNo     车辆号
     * @param assetTypeId 设备类型id
     * @return 该车辆、该设备类型、的入库明细
     */
    List<BuOutsourceOutinDetail> selectInDetailListByTrainNoAndAssetTypeId(@Param("trainNo") String trainNo, @Param("assetTypeId") String assetTypeId, @Param("assetName") String assetName);

    /**
     * 根据工单任务id查询工单任务下的委外入库明细信息
     *
     * @param orderTaskId 工单任务id
     * @return 工单任务下的委外入库明细信息
     */
    List<BuOutsourceOutinDetail> selectInDetailListByOrderTaskId(@Param("orderTaskId") String orderTaskId);

    /**
     * 根据委外出入库明细id列表查询委外出入库明细
     *
     * @param idList 委外出入库明细id列表
     * @return 委外出入库明细
     */
    List<BuOutsourceOutinDetail> selectListByIdList(@Param("idList") List<String> idList);

    /**
     * 根据合同id查找合同完成时间和质保月数
     *
     * @param contractId the contract id
     * @return the map
     */
    Map<String, Object> selectByContractId(@Param("contractId") String contractId);

}
