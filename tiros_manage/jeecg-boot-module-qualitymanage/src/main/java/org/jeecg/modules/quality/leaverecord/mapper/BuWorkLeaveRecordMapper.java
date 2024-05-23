package org.jeecg.modules.quality.leaverecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.leaverecord.bean.BuWorkLeaveRecord;
import org.jeecg.modules.quality.leaverecord.bean.vo.BuWorkLeaveRecordQueryVO;

import java.util.List;

/**
 * <p>
 * 开口项 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -10-21
 */
public interface BuWorkLeaveRecordMapper extends BaseMapper<BuWorkLeaveRecord> {

    /**
     * 分页查询开口项记录
     *
     * @param page    分页信息
     * @param queryVO the query vo
     * @return 分页结果 page
     */
    IPage<BuWorkLeaveRecord> selectWorkLeaveRecordPage(IPage<BuWorkLeaveRecord> page, @Param("queryVO") BuWorkLeaveRecordQueryVO queryVO);

    /**
     * Update status.
     *
     * @param id the id
     */
    void updateStatus(@Param("id") String id);

    /**
     * 根据条件查询开口项list
     *
     * @param queryVO 查询条件
     * @return 开口项list
     */
    List<BuWorkLeaveRecord> selectListByCondition(@Param("queryVO") BuWorkLeaveRecordQueryVO queryVO);
}
