package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysAnnouncement;

import java.util.List;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
public interface SysAnnouncementMapper extends BaseMapper<SysAnnouncement> {


    List<SysAnnouncement> querySysCementListByUserId(Page<SysAnnouncement> page, @Param("userId") String userId, @Param("msgCategory") String msgCategory);

    /**
     * 查询用户最新通告
     *
     * @param userId 用户id
     * @param number 通告条数
     * @return 通告列表
     */
    List<SysAnnouncement> selectListByUserIdAndNumber(@Param("userId") String userId, @Param("number") Integer number);

    /**
     * 分页查询用户通告
     *
     * @param page   分页信息
     * @param userId 用户id
     * @return 分页结果
     */
    IPage<SysAnnouncement> selectPageByUserId(IPage<SysAnnouncement> page, @Param("userId") String userId, @Param("title") String title);

    /**
     * 根据id查询通告详情
     *
     * @param id     通告id
     * @param userId 用户id
     * @return 通告详情
     */
    SysAnnouncement selectAnnouncementByIdAndUserId(@Param("id") String id, @Param("userId") String userId);

}
