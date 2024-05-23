package org.jeecg.modules.basemanage.track.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.track.entity.BuMtrTrack;
import org.jeecg.modules.basemanage.track.entity.vo.BuMtrTrackQueryVO;

/**
 * <p>
 * 股道信息 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
public interface IBuMtrTrackService extends IService<BuMtrTrack> {

    /**
     * 根据条件分页查询股道信息
     *
     * @param page  分页信息
     * @param track 查询条件
     * @return 股道信息列表
     * @throws Exception 异常信息
     */
    Page<BuMtrTrack> selectTrackPage(Page<BuMtrTrack> page, BuMtrTrackQueryVO track) throws Exception;

    /**
     * 新增股道信息
     *
     * @param buMtrTrack 股道信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveTrack(BuMtrTrack buMtrTrack) throws Exception;

    /**
     * 修改股道信息
     *
     * @param buMtrTrack 股道信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateTrack(BuMtrTrack buMtrTrack) throws Exception;

    /**
     * 批量删除股道信息
     *
     * @param ids 股道ids 多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatchByTrackIds(String ids) throws Exception;

}
