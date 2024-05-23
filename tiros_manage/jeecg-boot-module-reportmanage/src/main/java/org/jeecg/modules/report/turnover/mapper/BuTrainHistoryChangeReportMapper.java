package org.jeecg.modules.report.turnover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.turnover.bean.BuTrainHistoryChange;
import org.jeecg.modules.report.turnover.bean.vo.TurnoverDetailQueryVO;

import java.util.Date;

/**
 * <p>
 * 车辆履历-更换记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryChangeReportMapper extends BaseMapper<BuTrainHistoryChange> {

    /**
     * 根据条件查询车辆履历更换记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 更换记录分页
     */
    IPage<BuTrainHistoryChange> selectPageByCondition(IPage<BuTrainHistoryChange> page, @Param("queryVO") TurnoverDetailQueryVO queryVO);

    /**
     * 查询车辆最后一次接车记录的时间
     *
     * @param trainNo 车辆号
     * @return 车辆最后一次接车记录的时间
     */
    Date selectTrainLastReceiveTime(@Param("trainNo") String trainNo);

}
