package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskTrack;

import java.util.List;

/**
 * <p>
 * 任务停放，子任务没有设置的话，就继承父任务的设置，如果有设置则覆盖父任务的设置 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairTaskTrackMapper extends BaseMapper<BuRepairTaskTrack> {

//    /**
//     * 查询列计划任务中时间最早的列计划任务的股道
//     *
//     * @param planTaskIdList 列计划任务id列表
//     * @return 时间最早的列计划任务的股道
//     */
//    BuRepairTaskTrack selectTimeFirstTaskTrack(@Param("planTaskIdList") List<String> planTaskIdList);

    /**
     * 更新车辆当前股道
     *
     * @param trainNo 车辆号
     * @param trackId 股道id
     */
    void updateTrainTrack(@Param("trainNo") String trainNo, @Param("trackId") String trackId);

    /**
     * 清除车辆当前股道
     *
     * @param trainNo 车辆号
     * @param trackId 股道id
     */
    void clearTrainTrack(@Param("trainNo") String trainNo, @Param("trackId") String trackId);

    /**
     * 清除车辆当前股道
     *
     * @param trainNo     车辆号
     * @param trackIdList 股道id列表
     */
    void clearTrainTrackList(@Param("trainNo") String trainNo, @Param("trackIdList") List<String> trackIdList);

}
