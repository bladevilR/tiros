package org.jeecg.modules.group.information.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.material.material.bean.BuMaterialType;

import java.util.List;

/**
 * <p>
 * 车间班组信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
public interface BuMaterialToolsGroupService extends IService<BuMaterialTools> {

    /**
     * 添加班组工装/工具
     *
     * @param toolsId 工装id/工具id
     * @param groupId 班组id
     * @return 是否操作成功
     * @throws Exception 异常信息
     */
    boolean addToolsToGroup(List<String> toolsId, String groupId) throws Exception;

    /**
     * 删除班组工装/工具关联关系
     *
     * @param  toolsId 工装id/工具id
     * @param groupId 班组id
     * @return 是否操作成功
     * @throws Exception 异常信息
     */
    boolean deleteToolsFromGroup(String toolsId, String groupId) throws Exception;

}
