package org.jeecg.common.tiros.config.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.tiros.config.bean.SysConfig;

import java.util.Date;

/**
 * <p>
 * 系统配置 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-11
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 分页查询系统配置项
     *
     * @param searchText 编码或名称
     * @param pageNo     页面
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<SysConfig> pageConfig(String searchText, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 批量删除系统配置项
     *
     * @param codes 系统配置项codes，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String codes) throws Exception;

    /**
     * 获取任务最后一次执行时间
     *
     * @param configCode 任务硬编码code=系统配置的id
     * @return 最后一次执行时间
     */
    Date getScheduleTaskLastTime(String configCode);

    /**
     * 更新任务最后一次执行时间，删除再插入
     *
     * @param code 任务硬编码code=系统配置的id
     * @param name 系统配置名称
     * @param now  最后一次执行时间
     */
    void updateScheduleTaskLastTime(String code, String name, Date now);

    /**
     * 更新安全运营天数
     *
     * @param configCode 任务硬编码code=系统配置的id
     * @param days       天数
     */
    void updateScheduleTaskLastDays(String configCode, String days);

    /**
     * 获取系统配置值
     *
     * @param configCode 任务硬编码code=系统配置的id
     * @return 天数
     */
    String getConfigValueByCode(String configCode);

}
