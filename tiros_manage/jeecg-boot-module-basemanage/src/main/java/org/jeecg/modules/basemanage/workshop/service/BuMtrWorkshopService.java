package org.jeecg.modules.basemanage.workshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrWorkshop;

import java.util.List;

/**
 * <p>
 * 架修车间 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
public interface BuMtrWorkshopService extends IService<BuMtrWorkshop> {

    /**
     * 获取所有车间列表
     *
     * @return 车间列表
     * @throws Exception 异常信息
     */
    List<BuMtrWorkshop> listAll() throws Exception;

    /**
     * 根据id获取车间信息
     *
     * @param id 车间id
     * @return 车间信息
     * @throws Exception 异常信息
     */
    BuMtrWorkshop getWorkshopById(String id) throws Exception;

    /**
     * 删除车间（批量）
     *
     * @param ids 车间ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
