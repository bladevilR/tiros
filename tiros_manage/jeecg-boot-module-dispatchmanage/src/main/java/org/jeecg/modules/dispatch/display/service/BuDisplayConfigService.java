package org.jeecg.modules.dispatch.display.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.display.bean.BuDisplayConfig;

import java.util.List;

/**
 * <p>
 * 播放配置 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
public interface BuDisplayConfigService extends IService<BuDisplayConfig> {

    /**
     * 设置指定媒体大屏到已有的看板内容
     *
     * @param screenId   大屏id
     * @param resourceId 看板资源id
     * @return 是否设置成功
     * @throws Exception 异常信息
     */
    boolean setResourceToScreen(String screenId, String resourceId) throws Exception;

    /**
     * 上传媒体文件，并设置到指定媒体屏幕
     *
     * @param filePath 媒体文件路径
     * @param title    资源标题
     * @param screenId 大屏id
     * @return 是否设置成功
     * @throws Exception 异常信息
     */
    boolean setCustomToScreen(String filePath, String title, String screenId) throws Exception;

    /**
     * 获取指定大屏的当前播放内容
     *
     * @param screenId     大屏id
     * @param resourceType 资源类型
     * @return 当前播放内容列表
     * @throws Exception 异常信息
     */
    List<BuDisplayConfig> listPlayContent(String screenId, Integer resourceType) throws Exception;

    /**
     * 批量删除播放配置
     *
     * @param ids 播放配置ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
