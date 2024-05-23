package org.jeecg.modules.group.information.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;

/**
 * <p>
 * 车间班组 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-18
 */
public interface BuMtrWorkshopGroupService extends IService<BuMtrWorkshopGroup> {

    /**
     * 根据工班id获取工班基本信息
     *
     * @param groupId 工班id
     * @return 工班基本信息
     * @throws Exception 异常信息
     */
    BuMtrWorkshopGroup getGroupById(String groupId) throws Exception;


    /**
     * 设置班组班组副班长
     *
     * @param groupId 工班id，用户id，类型 1 班组 2 副班长
     * @return 工班基本信息
     * @throws Exception 异常信息
     */
    boolean saveWorkGroupUser(String groupId, String userId, String type) throws Exception;
}
