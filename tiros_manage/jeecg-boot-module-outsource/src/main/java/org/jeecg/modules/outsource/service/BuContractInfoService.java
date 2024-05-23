package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.*;
import org.jeecg.modules.outsource.bean.vo.*;

import java.util.List;

/**
 * <p>
 * 合同信息 服务类
 * </p>
 *
 * @author youGen
 * @since 2020 -10-16
 */
public interface BuContractInfoService extends IService<BuContractInfo> {

    /**
     * 新增合同
     *
     * @param contractInfo 合同
     * @return 是否成功 boolean
     * @throws Exception 异常
     */
    boolean saveContractInfo(BuContractInfo contractInfo) throws Exception;

    /**
     * 修改合同
     *
     * @param contractInfo 合同
     * @return 是否成功 boolean
     * @throws Exception 异常
     */
    boolean editContractInfo(BuContractInfo contractInfo) throws Exception;

    /**
     * 分页查询
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return page
     */
    IPage<BuContractInfo> page(BuContractInfoQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * 删除
     *
     * @param ids the ids
     * @return boolean
     * @throws Exception the exception
     */
    boolean removeBachByIds(List<String> ids) throws Exception;

    /**
     * 合同数据
     *
     * @param id the id
     * @return contract monitor
     */
    ContractMonitor contractMonitor(String id);

    /**
     * 委外质保
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return page
     */
    IPage<BuOutsourceQuality> qualityPage(BuOutsourceQualityQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * 委外部件统计
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return page
     */
    IPage<BuOutsourceAsset> partsPage(BuOutsourceAssetQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * 获取设备类型维修所需天数.
     *
     * @param assetTypeId 设备类型id
     * @return 维修所需天数 asset type need day
     */
    Integer getAssetTypeNeedDay(String assetTypeId);

    /**
     * 更新委外质保部件.
     *
     * @param qualityVO the quality vo
     * @return the boolean
     */
    Boolean updateQuality(BuOutsourceOutInQualityVO qualityVO) throws Exception;

    /**
     * Gets contract price.
     *
     * @param ids the as list
     * @return the contract price
     */
    List<ContractPrice> getContractPrice(List<String> ids) throws Exception;
}
