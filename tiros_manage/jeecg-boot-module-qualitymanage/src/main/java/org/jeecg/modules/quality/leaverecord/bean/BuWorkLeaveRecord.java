package org.jeecg.modules.quality.leaverecord.bean;

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
 * 开口项
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "开口项对象", description = "开口项")
@TableName("bu_work_leave_record")
public class BuWorkLeaveRecord extends Model<BuWorkLeaveRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编号  后端生成")
    private String recordCode;

    @ApiModelProperty(value = "开口项目名称", required = true)
    @NotBlank
    private String recordName;

    @ApiModelProperty(value = "开口项目描述")
    private String recordDesc;

    @ApiModelProperty(value = "所属线路id  字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "所属车辆编号  字典dictCode=(bu_train_info,train_no,train_no,line_id='{所属线路id}')", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "所属工单id  字典dictCode=(bu_work_order,order_name,id)")
    private String orderId;

    @ApiModelProperty(value = "所属工单任务id  字典dictCode=(bu_work_order_task,task_name,id,order_id='{所属工单id}')")
    private String orderTaskId;

    @ApiModelProperty(value = "开口项状态 0未处理 1已处理  字典dictCode=bu_repair_leave_status", required = true)
    @NotNull
    @Dict(dicCode = "bu_repair_leave_status")
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
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


    @ApiModelProperty(value = "所属工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "所属工单任务名称")
    @TableField(exist = false)
    private String orderTaskName;
    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
