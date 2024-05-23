package org.jeecg.modules.dispatch.workorder.bean;

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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 作业记录表，1 这里只存放作业记录表的填写信息，其他表单不需要保存到这里，2 如果在计划中是唯一的，则不关联任务， 如果
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "工单作业记录对象", description = "作业记录表，1 这里只存放作业记录表的填写信息，其他表单不需要保存到这里，2 如果在计划中是唯一的，则不关联任务， 如果")
@TableName("bu_work_order_record")
public class BuWorkOrderRecord extends Model<BuWorkOrderRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属作业记录表id")
    private String workRecId;

    @ApiModelProperty(value = "所属工单id")
    private String orderId;

    @ApiModelProperty(value = "所属工单表单id 表示关联工单的哪个作业表单记录id")
    private String orderFormId;

    @ApiModelProperty(value = "所属工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "所属列计划id")
    private String planId;

    @ApiModelProperty(value = "所属列计划任务id")
    private String planTaskId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "表单序号 当需要填写多份时的序号")
    private Integer formIndex;

    @ApiModelProperty(value = "填写状态 0未填写1已填写  字典dictCode=bu_form_result_status")
    @Dict(dicCode = "bu_form_result_status")
    private Integer status;

    @ApiModelProperty(value = "作业日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    @ApiModelProperty(value = "完工日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishDate;

    @ApiModelProperty(value = "作业班组id")
    private String workGroupId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "检查结果 0未通过1通过  字典dicCode=bu_work_order_record_result")
    @Dict(dicCode = "bu_work_order_record_result")
    private Integer checkResult;

    @ApiModelProperty(value = "检查日期 yyyy-MM-dd")
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

    @ApiModelProperty(value = "所属作业记录表名称")
    @TableField(exist = false)
    private String workRecName;

    @ApiModelProperty(value = "设备类型id", notes = "来自bu_work_order_record_asset")
    @TableField(exist = false)
    private String assetTypeId;

    @ApiModelProperty(value = "设备类型名称", notes = "来自bu_work_order_record_asset")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "车辆结构明细id", notes = "来自bu_work_order_record_asset")
    @TableField(exist = false)
    private String structDetailId;

    @ApiModelProperty(value = "车辆结构明细名称", notes = "来自bu_work_order_record_asset")
    @TableField(exist = false)
    private String structDetailName;

    @ApiModelProperty(value = "设备id", notes = "来自bu_work_order_record_asset")
    @TableField(exist = false)
    private String trainAssetId;

    @ApiModelProperty(value = "设备名称", notes = "来自bu_work_order_record_asset")
    @TableField(exist = false)
    private String trainAssetName;

    @ApiModelProperty(value = "作业记录表明细信息")
    @TableField(exist = false)
    private List<BuWorkOrderRecordDetail> orderRecordDetails;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
