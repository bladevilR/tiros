package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainHistoryChange;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainAssetUseRecordVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryChangeQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.HistoryRecordsQueryVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车辆履历-更换记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryChangeMapper extends BaseMapper<BuTrainHistoryChange> {

    /**
     * 批量插入
     *
     * @param list 更换记录列表
     */
    void insertList(@Param("list") List<BuTrainHistoryChange> list);

    /**
     * 根据条件查询车辆履历更换记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 更换记录分页
     */
    IPage<BuTrainHistoryChange> selectPageByCondition(IPage<BuTrainHistoryChange> page, @Param("queryVO") BuTrainHistoryChangeQueryVO queryVO);

    /**
     * 根据条件查询车辆履历更换记录列表，返回简单信息，用于分析
     *
     * @param queryVO 查询条件
     * @return 更换记录简单信息列表
     */
    List<BuTrainHistoryChange> selectSimpleListByCondition(@Param("queryVO") HistoryRecordsQueryVO queryVO);

    /**
     * 从周转件更换查询需归档的作业记录
     *
     * @param date 上次归档时间
     * @return 更换记录列表
     */
    List<BuTrainHistoryChange> selectListNeedCollect(@Param("date")Date date);

    /**
     * 查询车辆设备履历使用记录(更换记录)
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 车辆设备使用记录
     */
    IPage<BuTrainAssetUseRecordVO> selectAssetUseRecordVOPage(IPage<BuTrainHistoryChange> page, @Param("queryVO") BuTrainHistoryChangeQueryVO queryVO);

}
