package org.jeecg.modules.group.measurealert.bean;

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
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划表单实列(记录表)
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPlanFormDataRecord对象", description = "列计划表单实列(记录表)")
@TableName("bu_pl_data_record")
public class BuPlanFormDataRecord extends Model<BuPlanFormDataRecord> {
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "列计划表单id 来自列计划表单表主键", required = true)
    @NotBlank
    private String planFormId;

    @ApiModelProperty(value = "表单id 来自数据记录表表主键", required = true)
    @NotBlank
    private String formObjId;

    @ApiModelProperty(value = "第几份 当多份时的序号")
    private Integer formIndex;

    @ApiModelProperty(value = "车辆编号", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "设备类型id 来自车型设备类型表id")
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构明细id 来自车辆结构明细id")
    private String structDetailId;

    @ApiModelProperty(value = "设备id 来自车辆设备表id")
    private String trainAssetId;

    @ApiModelProperty(value = "填写状态 字典dictCode=bu_form_result_status")
    @Dict(dicCode = "bu_form_result_status")
    private Integer status;

    @ApiModelProperty(value = "填写日期")
    private Date writeDate;

    @ApiModelProperty(value = "填写人员id")
    private String writeUserId;

    @ApiModelProperty(value = "结果 为json，excel表单和自定义控件表单都是json，后端程序开启另一个线程从json抽取数据，保存到测量记录表，如果再次保存，则清除原所有记录（包括内存表中预警记录），重新生成")
    private String result;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "检查结果 字典dicCode=bu_work_order_record_result")
    @Dict(dicCode = "bu_work_order_record_result")
    private Integer checkResult;

    @ApiModelProperty(value = "检查日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;

    @ApiModelProperty(value = "质量负责人id")
    private String checkUserId;

    @ApiModelProperty(value = "标题")
    private String title;


    @ApiModelProperty(value = "填写班组id 保存结果时填入")
    @TableField(exist = false)
    private String writeGroupId;

    @ApiModelProperty(value = "工单id 保存结果时填入")
    @TableField(exist = false)
    private String orderId;

    @ApiModelProperty(value = "工单任务id 保存结果时填入")
    @TableField(exist = false)
    private String orderTaskId;

    @ApiModelProperty(value = "表单对象名称")
    @TableField(exist = false)
    private String formObjName;

    @ApiModelProperty(value = "填写人员名称")
    @TableField(exist = false)
    private String writeUserName;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "车辆结构名称")
    @TableField(exist = false)
    private String structDetailName;

    @ApiModelProperty(value = "设备名称")
    @TableField(exist = false)
    private String trainAssetName;

    @ApiModelProperty(value = "数据值记录集合")
    @TableField(exist = false)
    private List<BuPlanFormValues> valuesList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
