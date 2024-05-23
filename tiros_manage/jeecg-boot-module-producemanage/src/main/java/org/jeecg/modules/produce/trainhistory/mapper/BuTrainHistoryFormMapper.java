package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainHistoryFormRecordVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryFormQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.HistoryRecordsQueryVO;

/**
 * <p>
 * 车辆履历-表单记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-26
 */
public interface BuTrainHistoryFormMapper {

    /**
     * 查询车辆履历相关表单记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 表单记录
     */
    IPage<BuTrainHistoryFormRecordVO> selectPageByCondition(IPage<BuTrainHistoryFormRecordVO> page, @Param("queryVO") BuTrainHistoryFormQueryVO queryVO);

}
