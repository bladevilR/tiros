package org.jeecg.modules.quality.measureanalyse.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;

import java.util.List;

/**
 * <p>
 * 测量值分析查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/20
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuMeasureAnalyseQueryVO extends TimeQuery {

    @ApiModelProperty(value = "线路(所属线路id)   字典dictCode=(bu_mtr_line,line_name,line_id)")
    private String lineId;

    @ApiModelProperty(value = "车辆(所属车辆编号)   字典dictCode=(bu_train_info,train_no,train_no,line_id={所属线路id})")
    private String trainNo;

    @ApiModelProperty(value = "测量项id列表")
    private List<String> itemIdList;

}
