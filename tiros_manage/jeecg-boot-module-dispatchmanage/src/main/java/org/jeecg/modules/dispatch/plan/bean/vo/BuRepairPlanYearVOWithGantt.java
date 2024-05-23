package org.jeecg.modules.dispatch.plan.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.bean.PlusProjectGantt;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 年计划 甘特图对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-20
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuRepairPlanYearVOWithGantt extends PlusProjectGantt<BuRepairPlanYearDetailVOWithTaskGantt> {

    @ApiModelProperty(value = "年计划id")
    private String id;

    @ApiModelProperty(value = "年计划标题")
    private String title;

    @ApiModelProperty(value = "计划年份")
    private Integer year;

    @ApiModelProperty(value = "首列车架修时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstTime;

    @ApiModelProperty(value = "所属车辆段id   字典dictCode=(bu_mtr_depot,name,id)")
    private String depotId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 字典dictCode=bu_repair_plan_status")
    @Dict(dicCode = "bu_repair_plan_status")
    private Integer status;

    @ApiModelProperty(value = "架修数量")
    private Integer middleAmount;

    @ApiModelProperty(value = "大修数量")
    private Integer hightAmount;

    @ApiModelProperty(value = "架修模板id")
    private String middlePlanTemplate;

    @ApiModelProperty(value = "大修模板id")
    private String hightPlanTemplate;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;

    @ApiModelProperty(value = "更新日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "更新人员")
    private String updateBy;

    @ApiModelProperty(value = "车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "架修模板名称")
    private String middlePlanTemplateName;

    @ApiModelProperty(value = "大修模板名称")
    private String hightPlanTemplateName;

    @ApiModelProperty(value = "所属车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "车间名称")
    private String workshopName;

    @ApiModelProperty(value = "架修完成数")
    private Integer middleRepairFinish;

    @ApiModelProperty(value = "大修完成数")
    private Integer hightRepairFinish;

}
