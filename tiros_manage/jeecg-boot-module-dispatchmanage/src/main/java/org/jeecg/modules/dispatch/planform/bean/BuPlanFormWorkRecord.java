package org.jeecg.modules.dispatch.planform.bean;

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
import org.jeecg.modules.dispatch.serialplan.bean.bo.BuWorkRecordCategoryBO;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划表单实列(作业记录)
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPlanFormWorkRecord对象", description = "列计划表单实列(作业记录)")
@TableName("bu_pl_work_record")
public class BuPlanFormWorkRecord extends Model<BuPlanFormWorkRecord> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属列计划id")
    private String planId;

    @ApiModelProperty(value = "列计划表单id", required = true)
    @NotBlank
    private String planFormId;

    @ApiModelProperty(value = "表单对象id", required = true)
    @NotBlank
    private String formObjId;

    @ApiModelProperty(value = "车号", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构明细id")
    private String structDetailId;

    @ApiModelProperty(value = "原设备id")
    private String trainAssetId;

    @ApiModelProperty(value = "原部件号")
    private String manufNo;

    @ApiModelProperty(value = "现设备id")
    private String trainAssetIdUp;

    @ApiModelProperty(value = "现部件号")
    private String manufNoUp;

    @ApiModelProperty(value = "表单序号", required = true)
    @NotNull
    private Integer formIndex;

    @ApiModelProperty(value = "填写状态 字典dictCode=bu_form_result_status")
    @Dict(dicCode = "bu_form_result_status")
    private Integer status;

    @ApiModelProperty(value = "作业日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    @ApiModelProperty(value = "完工日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishDate;

    @ApiModelProperty(value = "作业班组id")
    private String workGroupId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "检查结果 字典dicCode=bu_work_order_record_result")
    @Dict(dicCode = "bu_work_order_record_result")
    private Integer checkResult;

    @ApiModelProperty(value = "检查日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;

    @ApiModelProperty(value = "质量负责人id")
    private String checkUserId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表")
    private Integer workRecordType;


    @ApiModelProperty(value = "所属作业记录表名称")
    @TableField(exist = false)
    private String workRecName;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "车辆结构明细名称")
    @TableField(exist = false)
    private String structDetailName;

    @ApiModelProperty(value = "原设备名称")
    @TableField(exist = false)
    private String trainAssetName;

    @ApiModelProperty(value = "现设备名称")
    @TableField(exist = false)
    private String trainAssetNameUp;

    @ApiModelProperty(value = "作业班组名称")
    @TableField(exist = false)
    private String workGroupName;

    @ApiModelProperty(value = "质量负责人名称")
    @TableField(exist = false)
    private String checkUserName;

    @ApiModelProperty(value = "是否拆装  字典dicCode=bu_state")
    @Dict(dicCode = "bu_state")
    @TableField(exist = false)
    private Integer updown;

    @ApiModelProperty(value = "记录明细列表")
    @TableField(exist = false)
    private List<BuPlanFormWorkRecordDetail> detailList;

    @ApiModelProperty(value = "记录明细分类列表")
    @TableField(exist = false)
    private List<BuWorkRecordCategoryBO> categoryList;

    @ApiModelProperty(value = "选择的填写明细")
    @TableField(exist = false)
    private String recordIds;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
