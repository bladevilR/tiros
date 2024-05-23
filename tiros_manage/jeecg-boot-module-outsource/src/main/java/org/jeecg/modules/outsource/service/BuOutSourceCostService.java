package org.jeecg.modules.outsource.service;

import org.jeecg.modules.outsource.bean.OutsourceAsset;
import org.jeecg.modules.outsource.bean.OutsourceCost;
import org.jeecg.modules.outsource.bean.OutsourceCostDetail;
import org.jeecg.modules.outsource.bean.OutsourceCostPart;

import java.util.List;

/**
 * <p>
 * 委外成本 服务类
 * </p>
 *
 * @author youGen
 * @since 2021-11-26
 */
public interface BuOutSourceCostService {

    /**
     * 委外成本（所有 ，按线路和修程维度：总成本，单列成本，单节成本）
     *
     * @return 委外成本
     * @throws  Exception   异常
     */
    List<OutsourceCost> listCost() throws Exception;

    /**
     * 根据线路和修程查委外成本明细
     *
     * @param lineId          线路id
     * @param repairProgramId 修程id
     * @return 成本明细
     * @throws  Exception   异常
     */

    List<OutsourceCostDetail> outsourceCostDetail(String lineId, String repairProgramId) throws Exception;

   /* *//**
     * 根据线路查具体部件合同价格趋势
     *
     * @param type   类型
     * @param lineId  线路id
     * @return 部件价格
     * @throws  Exception   异常
     *//*
    List<OutsourceCostPart> outsourceCostPart(String lineId,Integer type) throws Exception;*/

    /**
     * 查询合同设备
     * @return 合同设备
     * @throws  Exception   异常
     */
    List<OutsourceAsset> outsourceAssetList() throws Exception;;

    /**
     *  根据合同设备id找合同价格趋势
     * @param assetId 设备id
     * @param type  类型
     * @return 合同价格
     * @throws Exception
     */
    List<OutsourceCostPart> outsourceCostPartByAssetId(String assetId, Integer type) throws Exception;
}
