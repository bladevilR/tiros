package org.jeecg.modules.material.cost.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.cost.bean.vo.CompareInfoVO;
import org.jeecg.modules.material.cost.bean.vo.CompareQueryVO;
import org.jeecg.modules.material.cost.bean.vo.CompareResultVO;

import java.util.List;

/**
 * <p>
 * 物料消耗对比 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
public interface MaterialCostCompareService {

    /**
     * 根据条件查询物料消耗对比
     *
     * @param queryVO 查询条件
     * @return 消耗对比结果
     * @throws Exception 异常
     */
    CompareInfoVO listMaterialCostCompare(CompareQueryVO queryVO, boolean forExport) throws Exception;

    /**
     * 根据工单物料ids查询工单物料
     *
     * @param orderMaterialIds 工单物料ids，多个逗号分隔
     * @return 工单物料
     * @throws Exception 异常
     */
    List<BuWorkOrderMaterial> listOrderMaterialByIds(String orderMaterialIds) throws Exception;

    /**
     * 批量核实工单物料，只改核实状态
     *
     * @param orderMaterialIds 工单物料ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean verifyOrderMaterialBatch(String orderMaterialIds) throws Exception;

    /**
     * 单条数据核实，接收修改后的工单物料+实际消耗
     *
     * @param orderMaterialList 工单物料列表
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean verifyOrderMaterialList(List<BuWorkOrderMaterial> orderMaterialList) throws Exception;

    /**
     * 根据条件查询物料消耗对比，并以excel文件导出
     *
     * @param queryVO 查询条件
     * @return excel文件
     * @throws Exception 异常
     */
    HSSFWorkbook exportMaterialCostCompare(CompareQueryVO queryVO) throws Exception;

}
