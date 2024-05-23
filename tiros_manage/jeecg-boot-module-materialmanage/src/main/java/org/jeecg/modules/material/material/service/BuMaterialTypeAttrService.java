package org.jeecg.modules.material.material.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;

/**
 * <p>
 * 物资属性 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-08-07
 */
public interface BuMaterialTypeAttrService extends IService<BuMaterialTypeAttr> {
    /**
     * 根据物资id获取物资属性
     *
     * @param materialTypeId 物资id
     * @return 物资属性
     * @throws Exception 异常
     */
    BuMaterialTypeAttr getByMaterialTypeId(String materialTypeId) throws Exception;

    /**
     * 设置物资属性
     *
     * @param buMaterialTypeAttr 物资属性信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean setMaterialAttr(BuMaterialTypeAttr buMaterialTypeAttr) throws Exception;

}
