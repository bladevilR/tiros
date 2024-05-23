package org.jeecg.modules.outsource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.outsource.bean.BuContractInfo;
import org.jeecg.modules.outsource.bean.BuOutsourceAsset;
import org.jeecg.modules.outsource.bean.BuOutsourceQuality;
import org.jeecg.modules.outsource.bean.OutsourceAsset;
import org.jeecg.modules.outsource.bean.vo.BuContractInfoQueryVO;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceAssetQueryVO;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceQualityQueryVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 合同信息 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuContractInfoMapper extends BaseMapper<BuContractInfo> {

    /**
     * 更新合同车辆已完成数
     *
     * @param list 合同信息列表
     */
    void updateListFinishAmount(@Param("list") List<BuContractInfo> list);

    /**
     * 更新合同车辆质保开始结束日期
     *
     * @param list 合同信息列表
     */
    void updateListWarranty(@Param("list") List<BuContractInfo> list);

    /**
     * 分页查
     *
     * @param page
     * @param contractInfo
     * @return
     */
    IPage<BuContractInfo> selectPageByCondition(Page<BuContractInfo> page, @Param("contractInfo") BuContractInfoQueryVO contractInfo);

    BigDecimal selectSumPay(@Param("id") String id);

    BigDecimal selectSumBak(@Param("id") String id);

    /**
     * 委外质保
     *
     * @param page
     * @param quality
     * @return
     */
    IPage<BuOutsourceQuality> qualityPage(Page<Object> page, @Param("quality") BuOutsourceQualityQueryVO quality);

    /**
     * 委外部件统计
     *
     * @param page
     * @param asset
     * @return
     */
    IPage<BuOutsourceAsset> partsPage(Page<Object> page, @Param("asset") BuOutsourceAssetQueryVO asset);

    /**
     * 获取设备类型维修所需天数.
     *
     * @param assetTypeId 设备类型id
     * @return 维修所需天数
     */
    Integer getAssetTypeNeedDay(@Param("assetTypeId") String assetTypeId);

    /**
     * 根据线路修程时间查询未完成的合同
     *
     * @param lineId    线路id
     * @param programId 修程id
     * @param date      时间
     */
    List<BuContractInfo> selectUnFinishListByLineAndProgramAndDate(@Param("lineId") String lineId, @Param("programId") String programId, @Param("date") Date date);

    /**
     * 查询需要提醒质保金退款的合同
     *
     * @param date 时间
     * @return 合同列表
     */
    List<BuContractInfo> selectListNeedRemindRefund(@Param("date") Date date);

    /**
     * 根据时间获取合同
     * @param date
     * @return
     */
    List<BuContractInfo> selectListByDate(@Param("date") Date date);

    /**
     * 根据线路Id找组数
     * @param lineId 线路id
     * @return 组数
     */
    Integer selectLineGroup(@Param("lineId") String lineId);

    /**
     * 根据线路和修程找合同
     * @param lineId 线路id
     * @param repairProgramId  修程id
     * @return  合同信息
     */

    List<BuContractInfo> selectListByLineIdAndRepairProId(@Param("lineId")String lineId, @Param("repairProgramId")String repairProgramId);

    /**根据线路找合同
     * @param lineId 线路id
     * @return  合同信息
     */
    List<BuContractInfo> selectListByLineId(@Param("lineId")String lineId);

    /**
     * 合同所有设备
     * @return 合同设备
     */
    List<OutsourceAsset> selectAssetList();

    /**
     * 根据合同设备id找合同
     * @param assetId 合同设备
     * @return 合同信息
     */
    List<BuContractInfo> selectListByAssetId(@Param("assetId")String assetId);
}
