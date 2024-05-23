package org.jeecg.modules.quality.measurealert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.quality.measurealert.bean.vo.BuWorkMeasureAlertQueryVO;
import org.jeecg.modules.quality.measureanalyse.bean.vo.BuMeasureAnalyseQueryVO;

import java.util.List;

/**
 * <p>
 * 记录表数据值记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
public interface BuPlanFormValuesQualityMapper extends BaseMapper<BuPlanFormValues> {

    /**
     * 根据条件分页查询测量预警记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuPlanFormValues> selectPageByCondition(IPage<BuPlanFormValues> page, @Param("queryVO") BuWorkMeasureAlertQueryVO queryVO);

    /**
     * 根据条件查询测量记录表数据值记录列表
     *
     * @param queryVO 查询条件
     * @return 记录列表
     */
    List<BuPlanFormValues> selectListForAnalyseByCondition(@Param("queryVO") BuMeasureAnalyseQueryVO queryVO);

}
