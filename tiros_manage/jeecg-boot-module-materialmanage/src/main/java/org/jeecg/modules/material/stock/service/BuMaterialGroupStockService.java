package org.jeecg.modules.material.stock.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.material.apply.bean.BuWorkOrder;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialGroupStockQueryVO;
import org.jeecg.modules.material.stock.bean.vo.GroupStockAtrrVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 班组库存 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
public interface BuMaterialGroupStockService extends IService<BuMaterialGroupStock> {

    /**
     * 根据条件分页查询班组库存
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuMaterialGroupStock> pageGroupStock(BuMaterialGroupStockQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据班组id和物资类型id查询班组库存列表：用于作业填报查班组库存，没有会新增库存量为0的记录
     *
     * @param groupId         班组id
     * @param materialTypeId  物资类型id
     * @param orderMaterialId 工单物料id
     * @return 库存列表
     * @throws Exception 异常
     */
    List<BuMaterialGroupStock> listGroupStockForTaskWrite(String groupId, String materialTypeId, String orderMaterialId) throws Exception;

    /**
     * 根据班组id和物资类型id查询班组库存量
     *
     * @param groupId        班组id
     * @param materialTypeId 物资类型id
     * @return 库存量
     * @throws Exception 异常
     */
    Double getGroupMaterialStockAmount(String groupId, String materialTypeId) throws Exception;

    /**
     * 根据占用量计算班组库存
     *
     * @param groupStockList            班组库存
     * @param groupId                   班组i
     * @param needUsedOrderMaterialList 占用的工单物料
     * @param needIgnoreOrderMaterialId 需要忽略的工单物料id
     * @param needIgnoreOrderCodeList   需要忽略的工单号
     */
    void deleteGroupStockUsedAmount(List<BuMaterialGroupStock> groupStockList, String groupId, List<BuWorkOrderMaterial> needUsedOrderMaterialList, String needIgnoreOrderMaterialId, List<String> needIgnoreOrderCodeList);

    /**
     * 更新班组库存数据
     *
     * @param groupStockList 班组库存
     * @param order          工单
     * @param now            时间
     * @param sourceScene    使用场景
     * @param userId         人员id
     */
    void updateGroupStockByGroupStockListForMaterialCostCheck(List<BuMaterialGroupStock> groupStockList, BuWorkOrder order, Date now, String sourceScene, String userId);

    /**
     * 导出班组库存
     *
     * @return
     */
    HSSFWorkbook exportGroupStock(BuMaterialGroupStockQueryVO queryVO);

    /**
     * 设置班组库存属性
     *
     * @param stockAtrrVO 属性对象
     * @return boolean
     * @throws Exception 异常
     */
    Boolean setMaterialAttribute(GroupStockAtrrVO stockAtrrVO) throws Exception;

    /**
     * 恢复班组库存属性
     *
     * @param ids      班组库存id
     * @param groupIds 班组id
     * @return boolean
     * @throws Exception 异常
     */
    Boolean recoverMaterialAttribute(String ids, String groupIds) throws Exception;

    /**
     * 指定班组库存物资转入周转件
     *
     * @param groupStockId 班组库存id
     * @param transAmount  转入数量
     * @return 转入结果
     */
    String transGroupStockToTurnover(String groupStockId, BigDecimal transAmount);

}
