package org.jeecg.modules.basemanage.alertaccept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlert;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 预警信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-24
 */
public interface BuBaseAlertMapper extends BaseMapper<BuBaseAlert> {

    /**
     * 批量插入
     *
     * @param list 预警信息列表
     */
    void insertList(@Param("list") List<BuBaseAlert> list);

    /**
     * 查询物料库存预警
     *
     * @return 物料库存预警信息
     */
    List<Map<String, Object>> countMaterialStockAlert();

    /**
     * 查询物料到期预警
     * @param date                   需预警期限
     * @return 物料到期预警信息
     */
    List<Map<String, Object>> countMaterialExpireAlert(@Param("date")Date date);

    /**
     * 查询器具送检预警
     *
     * @param date                   需送检期限
     * @param notAlertToolStatusList 不需要预警的工器具状态
     * @return 器具送检预警数量
     */
    List<Map<String, Object>> countToolsCheckAlert(@Param("date") Date date, @Param("notAlertToolStatusList") List<Integer> notAlertToolStatusList);

    /**
     * 查询部件质保期预警
     *
     * @return 部件质保期预警
     */
    List<Map<String, Object>> countOutsourceTrainAssetExpireAlert(@Param("date") Date date);

    /**
     * 查询测量数据预警
     *
     * @return 测量数据预警数量
     */
    List<Map<String, Object>> countMeasureAlert();

//    /**
//     * 查询委外逾期
//     *
//     * @param date 查询时间
//     * @return 委外逾期数量
//     */
//    Integer countDelayOutsourceOrderTask(@Param("date") Date date);

    /**
     * 查询委外逾期
     *
     * @param date 查询时间
     * @return 委外逾期数量
     */
    List<Map<String, Object>> countDelayOutsourceOutinDetail(@Param("date") Date date);

    /**
     * 查询逾期工单
     *
     * @param date 查询时间
     * @return 逾期工单数量
     */
    List<Map<String, Object>> countDelayOrder(@Param("date") Date date);

    /**
     * 查询未处理故障数量
     *
     * @return 未处理故障数量
     */
    List<Map<String, Object>> countUnHandleFault();

}
