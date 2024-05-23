package org.jeecg.modules.material.cost.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.cost.bean.vo.CheckQueryVO;

/**
 * <p>
 * 列计划物料核实 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/15
 */
public interface PlanMaterialCheckService {

    /**
     * 分页查询工单物料
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuWorkOrderMaterial> pageOrderMaterial(CheckQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询工单物料详情
     *
     * @param orderMaterialId 工单物料id
     * @return 工单物料详情
     * @throws Exception 异常
     */
    BuWorkOrderMaterial getOrderMaterialDetail(String orderMaterialId) throws Exception;

    /**
     * 保存工单物料
     *
     * @param orderMaterial 工单物料
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveOrderMaterial(BuWorkOrderMaterial orderMaterial) throws Exception;

}
