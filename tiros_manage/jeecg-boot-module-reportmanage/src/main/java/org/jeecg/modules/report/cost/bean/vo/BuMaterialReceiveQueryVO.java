package org.jeecg.modules.report.cost.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 领料明细 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-01
 */
@Data
@Accessors(chain = true)
public class BuMaterialReceiveQueryVO {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "工单名称或编码")
    private String orderSearchText;

    @ApiModelProperty(value = "物料名称或编码")
    private String materialSearchText;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "是否全部消耗成功 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer syncResultSuccess;

}
