package org.jeecg.modules.dispatch.plan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 年计划
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "年计划对象", description = "年计划")
@TableName("bu_repair_plan_year")
public class BuRepairPlanYear extends Model<BuRepairPlanYear> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "年计划标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "计划年份", required = true)
    @NotNull
    private Integer year;

    @ApiModelProperty(value = "首列车架修时间 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstTime;

    @ApiModelProperty(value = "所属车辆段id   字典dictCode=(bu_mtr_depot,name,id)", required = true)
    @NotBlank
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

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "架修模板名称")
    @TableField(exist = false)
    private String middlePlanTemplateName;

    @ApiModelProperty(value = "大修模板名称")
    @TableField(exist = false)
    private String hightPlanTemplateName;

    @ApiModelProperty(value = "年计划明细列表")
    @TableField(exist = false)
    private List<BuRepairPlanYearDetail> detailList;

    @ApiModelProperty(value = "所属车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "车间名称")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "架修完成数")
    @TableField(exist = false)
    private Integer middleRepairFinish;

    @ApiModelProperty(value = "大修完成数")
    @TableField(exist = false)
    private Integer hightRepairFinish;

    @ApiModelProperty(value = "完成进度百分比 30表示30%")
    @TableField(exist = false)
    private Integer finishPercent;

    @ApiModelProperty(value = "审批流程状态")
    @TableField(exist = false)
    private String wfStatus;

    @ApiModelProperty(value = "流程当前处理人")
    @TableField(exist = false)
    private String processCandidate;

    @ApiModelProperty(value = "第几列", required = true)
    @NotNull
    @TableField(exist = false)
    private Integer trainIndex;

    @ApiModelProperty(value = "数量 默认1")
    @TableField(exist = false)
    private Integer amount;

    @ApiModelProperty(value = "开始日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date startDate;

    @ApiModelProperty(value = "结束日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date finishDate;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String programName;

    @ApiModelProperty(value = "年计划明细")
    @TableField(exist = false)
    private List<BuRepairPlanYearDetail> planYearDetailList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
