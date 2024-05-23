package org.jeecg.modules.dispatch.workorder.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderQueryVO {

    private String id;

    @ApiModelProperty(value = "工单编号或者名称")
    private String searchText;

    @ApiModelProperty(value = "状态(数字List) 字典bu_order_status 工班管理中用字典bu_group_order_status")
    private List<Integer> status;

    /*  由于调度和工班查询工单接口分开的，所以无需改参数 @ApiModelProperty(value = "工单创建来源，1 自动创建 2 调度创建  3  工班创建")
    private List<Integer> fromType;*/

    @ApiModelProperty(value = "班组 字典bu_mtr_workshop_group,group_name,id")
    private String workGroupId;

    @ApiModelProperty(value = "工单计划开始日期-开始")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "工单计划开始日期-结束")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "工单类型 字典 bu_order_type")
    private Integer orderType;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "是否逾期")
    private Integer overdue;

    @ApiModelProperty(value = "是否查询填报工单")
    private Boolean isWrite;

    @ApiModelProperty(value = "是否用于手动控制显示监控大屏显示数据")
    private Integer isForMonitorScreenControl;

    @ApiModelProperty(value = "列计划id列表，暂时用于手动控制显示监控大屏显示数据")
    private List<String> planIdList;

}
