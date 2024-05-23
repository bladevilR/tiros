package org.jeecg.modules.board.progress.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
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

/**
 * <p>
 * 年计划明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "年计划明细对象", description = "年计划明细")
@TableName("bu_repair_plan_year_detail")
public class BuRepairPlanYearDetail extends Model<BuRepairPlanYearDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属年计划id")
    private String yearPlanId;

    @ApiModelProperty(value = "所属线路id   字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "第几列", required = true)
    @NotNull
    private Integer trainIndex;

    @ApiModelProperty(value = "数量 默认1")
    private Integer amount;

    @ApiModelProperty(value = "修程id   字典dictCode=(bu_repair_program,name,id)", required = true)
    @NotBlank
    private String programId;

    @ApiModelProperty(value = "开始日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishDate;

    @ApiModelProperty(value = "状态 1未接车 2已接车 3已交车  dictCode=bu_repair_plan_yearDetail_status， 当对应的接车记录审批完成后更改该记录状态，对应的交车记录审批完成后更改该状态")
    @Dict(dicCode = "bu_repair_plan_yearDetail_status")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String programName;

    @ApiModelProperty(value = "所属车辆段id   字典dictCode=(bu_mtr_depot,name,id)", required = true)
    @TableField(exist = false)
    private String depotId;

    @ApiModelProperty(value = "所属车辆段名称")
    @TableField(exist = false)
    private String depotName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
