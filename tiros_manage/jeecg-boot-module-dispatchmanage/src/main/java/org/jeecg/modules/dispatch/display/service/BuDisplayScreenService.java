package org.jeecg.modules.dispatch.display.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.display.bean.BuDisplayScreen;

import java.util.List;

/**
 * <p>
 * 大屏信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
public interface BuDisplayScreenService extends IService<BuDisplayScreen> {

    /**
     * 根据大屏类型和班组id获取大屏信息列表
     *
     * @param screenType 大屏类型
     * @param groupId    班组id 大屏类型screenType=2时需要
     * @return 大屏信息列表
     * @throws Exception 异常信息
     */
    List<BuDisplayScreen> listByTypeAndGroupId(Integer screenType, String groupId) throws Exception;

}
