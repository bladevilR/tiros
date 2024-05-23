package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysAnnouncement;

import java.util.List;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
public interface ISysAnnouncementService extends IService<SysAnnouncement> {

    void saveAnnouncement(SysAnnouncement sysAnnouncement);

    boolean upDateAnnouncement(SysAnnouncement sysAnnouncement);

    void saveSysAnnouncement(String title, String msgContent);

    Page<SysAnnouncement> querySysCementPageByUserId(Page<SysAnnouncement> page, String userId, String msgCategory);

    /**
     * 查询用户最新通告
     *
     * @param number 通告条数，默认5
     * @return 通告列表
     * @throws Exception 异常信息
     */
    List<SysAnnouncement> listLastAnnouncement(Integer number) throws Exception;

    /**
     * 分页查询用户通告
     *
     * @param title    标题，模糊搜索
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<SysAnnouncement> pageUserAnnouncement(String title, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询通告详情
     *
     * @param id 通告id
     * @return 通告详情
     * @throws Exception 异常信息
     */
    SysAnnouncement getAnnouncementById(String id) throws Exception;

}
