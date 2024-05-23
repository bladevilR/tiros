package org.jeecg.modules.dispatch.plan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
import java.util.List;

/**
 * <p>
 * 远期规划
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "远期规划对象", description = "远期规划")
@TableName("bu_repair_plan_far")
public class BuRepairPlanFar extends Model<BuRepairPlanFar> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "规划标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "所属车辆段id", required = true)
    @NotBlank
    private String depotId;

    @ApiModelProperty(value = "开始年份", required = true)
    @NotNull
    private Integer startYear;

    @ApiModelProperty(value = "结束年份", required = true)
    @NotNull
    private Integer endYear;

    @ApiModelProperty(value = "架修总列数")
    private Integer middleRepair;

    @ApiModelProperty(value = "大修总列数")
    private Integer hightRepair;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 字典dictCode=bu_repair_plan_status")
    @Dict(dicCode = "bu_repair_plan_status")
    private Integer status;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "所属车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "车间名称")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "远期规划明细列表")
    @TableField(exist = false)
    private List<BuRepairPlanFarDetail> detailList;

    @ApiModelProperty(value = "审批流程状态")
    @TableField(exist = false)
    private String wfStatus;

    @ApiModelProperty(value = "流程当前处理人")
    @TableField(exist = false)
    private String processCandidate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
