package org.jeecg.modules.material.apply.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialAssignSaveVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialAutoAssignResultVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialAutoAssignVO;

import java.util.List;

/**
 * <p>
 * 物料分配明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuMaterialAssignDetailService extends IService<BuMaterialAssignDetail> {

    /**
     * 根据领用明细id查询物流分配明细
     *
     * @param applyDetailId 领用明细id
     * @return 物料分配明细列表
     * @throws Exception 异常
     */
    List<BuMaterialAssignDetail> listAssignDetail(String applyDetailId) throws Exception;

    /**
     * 自动分配物料（仅生成分配明细，不保存）
     *
     * @param autoAssignVO 自动分配物料信息
     * @return 生成结果消息
     * @throws Exception 异常
     */
    BuMaterialAutoAssignResultVO autoAssignAndSave(BuMaterialAutoAssignVO autoAssignVO) throws Exception;

}
