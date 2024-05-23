package org.jeecg.modules.basemanage.repair.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttribute;
import org.jeecg.modules.basemanage.repair.bean.vo.RepairAttributeQueryVO;

/**
 * <p>
 * 检修属性 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
public interface BuRepairAttributeService extends IService<BuRepairAttribute> {

    /**
     * 根据条件分页查询检修属性
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    IPage<BuRepairAttribute> pageRepairAttribute(RepairAttributeQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * 保存检修属性
     *
     * @param repairAttribute 检修属性
     * @return 是否成功
     */
    boolean saveRepairAttribute(BuRepairAttribute repairAttribute);

    /**
     * 批量删除检修属性
     *
     * @param ids 检修属性ids，多个逗号分隔
     * @return 是否成功
     */
    boolean deleteRepairAttribute(String ids);

}
