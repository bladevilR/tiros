package org.jeecg.modules.group.measurealert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.group.measurealert.bean.BuWorkMeasureItem;
import org.jeecg.modules.group.measurealert.bean.vo.BuWorkMeasureAlertQueryVO;

/**
 * <p>
 * 记录表数据值记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
public interface BuPlanFormValuesGroupMapper extends BaseMapper<BuPlanFormValues> {

    /**
     * 根据条件分页查询测量预警记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuPlanFormValues> selectPageByCondition(IPage<BuPlanFormValues> page, @Param("queryVO") BuWorkMeasureAlertQueryVO queryVO);

    /**
     * 根据测量项id查询测量项
     *
     * @param measureItemId 测量项id
     * @return 测量项信息
     */
    BuWorkMeasureItem selectMeasureItem(@Param("measureItemId") String measureItemId);

}
