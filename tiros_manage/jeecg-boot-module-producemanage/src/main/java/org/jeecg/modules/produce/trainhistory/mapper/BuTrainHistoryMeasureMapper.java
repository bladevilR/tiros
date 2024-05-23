package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryMeasure;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryMeasureQueryVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆履历-测量记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryMeasureMapper extends BaseMapper<BuTrainHistoryMeasure> {

    /**
     * 批量插入
     *
     * @param list 测量记录列表
     */
    void insertList(@Param("list") List<BuTrainHistoryMeasure> list);

    /**
     * 根据条件查询车辆履历测量记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 测量记录分页
     */
    IPage<BuTrainHistoryMeasure> selectPageByCondition(IPage<BuTrainHistoryMeasure> page, @Param("queryVO") BuTrainHistoryMeasureQueryVO queryVO);

    /**
     * 从记录表数据值记录查询需归档的作业记录
     *
     * @param date 上次归档时间
     * @return 测量记录列表
     */
    List<BuTrainHistoryMeasure> selectListNeedCollect(@Param("date")Date date);

}
