package org.jeecg.modules.produce.plan.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划详情VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/10
 */
@Data
@Accessors(chain = true)
public class BuRepairPlanVO {

    @ApiModelProperty(value = "计划id")
    private String id;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "计划名称")
    private String planName;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "车辆段id")
    private String depotId;

    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "车间名称")
    private String workshopName;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "进度状态 字典dictCode=bu_progress_status")
    @Dict(dicCode = "bu_progress_status")
    private Integer progressStatus;

    @ApiModelProperty(value = "当前进度：为1~100的数量，列计划中当前已完成任务的工期，占所有任务工期的比例，后端程序自动计算")
    private Integer progress;

    @ApiModelProperty(value = "计划开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "计划完工日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date finishDate;

    @ApiModelProperty(value = "当前工期第X天")
    private Integer currentDay;

    @ApiModelProperty(value = "统计信息")
    private List<BuRepairPlanCountItemVO> countItems;

}
