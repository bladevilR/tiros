package org.jeecg.modules.material.plan.bean;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author youGen
 * @since 2021-05-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_material_year_plan")
@ApiModel(value = "BuMaterialYearPlan对象", description = "")
public class BuMaterialYearPlan extends Model<BuMaterialYearPlan> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty(value = "年份", required = true)
    @NotNull
    private Integer sbYear;

    @ApiModelProperty(value = "线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "修程id", required = true)
    @NotBlank
    private String repairProgramId;

    @ApiModelProperty(value = "物类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "采用物资类型表中物资类的枚举：字典bu_material_type", required = true)
    @Dict(dicCode = "bu_material_type")
    @NotNull
    private Integer sbType;

    @ApiModelProperty(value = "提报数量，所需数量根据选择的年份、该物资一列车所需数量*该年年计划维修车辆数", required = true)
    @NotNull
    private Double sbAmount;

    @ApiModelProperty(value = "提报人id")
    private String sbUserId;

    @ApiModelProperty(value = "提报部门id")
    private String sbDeptId;

    @ApiModelProperty(value = "提报时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date sbDate;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty("线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty("物资名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty("物资编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty("物资描述")
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty("物资单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty("物资属性，字典bu_material_attr")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_attr")
    private Integer materialTypeCategory;

    @ApiModelProperty("修程名称")
    @TableField(exist = false)
    private String repairProgramName;

    @ApiModelProperty(value = "提报人名称")
    @TableField(exist = false)
    private String sbUserName;

    @ApiModelProperty(value = "提报部门名称")
    @TableField(exist = false)
    private String sbDeptName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
