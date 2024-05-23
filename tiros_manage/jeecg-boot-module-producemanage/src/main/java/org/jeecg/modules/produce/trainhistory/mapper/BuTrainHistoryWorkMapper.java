package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryWork;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryWorkQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.HistoryRecordsQueryVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆履历-作业记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryWorkMapper extends BaseMapper<BuTrainHistoryWork> {

    /**
     * 批量插入
     *
     * @param list 作业记录列表
     */
    void insertList(@Param("list") List<BuTrainHistoryWork> list);

    /**
     * 根据条件查询车辆履历作业记录
     *
     * @param queryVO 查询条件
     * @return 作业记录分页
     */
    IPage<BuTrainHistoryWork> selectPageByCondition(IPage<BuTrainHistoryWork> page, @Param("queryVO") BuTrainHistoryWorkQueryVO queryVO);

    /**
     * 从作业工单查询需归档的作业记录
     *
     * @param date 上次归档时间
     * @return 作业记录列表
     */
    List<BuTrainHistoryWork> selectListNeedCollect(@Param("date") Date date);

}
