package org.jeecg.modules.produce.plan.bean;

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
import org.jeecg.modules.produce.plan.bean.vo.BuPlanFormChoiceVO;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划表单，当列计划审批通过后，根据该配置表自动生成相关数据记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairPlanForms对象", description = "列计划表单，当列计划审批通过后，根据该配置表自动生成相关数据记录")
@TableName("bu_repair_plan_forms")
public class BuRepairPlanForms extends Model<BuRepairPlanForms> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "列计划id", required = true)
    @NotNull
    private String planId;

    @ApiModelProperty(value = "表单类型  字典dictCode=bu_work_form_type", required = true)
    @Dict(dicCode = "bu_work_form_type")
    @NotNull
    private Integer formType;

    @ApiModelProperty(value = "表单对象id", required = true)
    @NotBlank
    private String objId;

    @ApiModelProperty(value = "表单来源 1表示来自列计划生成2表示来自工单添加 当2时plan_id中存放工单的id当工单下发时自动生成表单实列（只有非自动生成的工单）")
    private Integer fromBy;

    @ApiModelProperty(value = "关联的设备类型id  表示这个表单用来记录哪个设备类型的，可以不填，需要填写多份的话最好选择设备类型，好自动匹配同一设备类型的不同设备。如果是记录表单，且有设置设备类型，说明对设备进行了拆装，那么则需要记录换上换下的设备部件号（出厂编码），如果对应得表单有设备类型id，则需要带入到这边")
    private String assetTypeId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "表单名称")
    @TableField(exist = false)
    private String formName;

    @ApiModelProperty(value = "在线表单(数据记录表)类型 formType=1在线表单(数据记录表)时的在线表单类型")
    @Dict(dicCode = "bu_form_type")
    @TableField(exist = false)
    private Integer type;

    @ApiModelProperty(value = "关联的设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "表单选择列表(根据formType：1为列计划记录表信息 3为列计划作业记录表信息)")
    @TableField(exist = false)
    private List<BuPlanFormChoiceVO> choiceList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
