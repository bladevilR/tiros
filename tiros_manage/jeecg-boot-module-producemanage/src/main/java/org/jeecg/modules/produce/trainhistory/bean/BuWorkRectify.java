package org.jeecg.modules.produce.trainhistory.bean;

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
 * 整改信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "整改信息对象", description = "整改信息")
@TableName("bu_work_rectify")
public class BuWorkRectify extends Model<BuWorkRectify> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "整改编号", required = true)
    @NotBlank
    private String rectifyNo;

    @ApiModelProperty(value = "整改标题", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "整改类型 1问题整改2技术整改3管理整改4其他整改  字典dictCode=bu_work_rectify_type", required = true)
    @NotNull
    @Dict(dicCode = "bu_work_rectify_type")
    private Integer rectifyType;

    @ApiModelProperty(value = "责任班组id", required = true)
    @NotBlank
    private String groupId;

    @ApiModelProperty(value = "责任人员id")
    private String dutyUserId;

    @ApiModelProperty(value = "下发日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sendDate;

    @ApiModelProperty(value = "整改状态 字典dictCode=bu_work_rectify_status")
    @Dict(dicCode = "bu_work_rectify_status")
    private Integer status;

    @ApiModelProperty(value = "整改工位id 字典dictCode=(bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id = '{工班id}')) 注意加''")
    private String workstationId;

    @ApiModelProperty(value = "关联工单id 字典dictCode=(bu_work_order,order_name,id)")
    private String orderId;

    @ApiModelProperty(value = "关联工单任务id 字典dictCode=(bu_work_order_task,task_name,id,order_id='{所属工单id}')")
    private String orderTaskId;

    @ApiModelProperty(value = "所属车辆段id 字典dictCode=(bu_mtr_depot,name,id)")
    private String depotId;

    @ApiModelProperty(value = "所属线路id 字典dictCode=(bu_mtr_line,line_name,line_id)")
    private String lineId;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "检查时间 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;
    @ApiModelProperty(value = "结果")
    private String changeResult;
    @ApiModelProperty(value = "车号")
    private String trainNo;
    @ApiModelProperty(value = "类型1 自建  2 检修,字典 bu_rectify_type")
    @Dict(dicCode = "bu_rectify_type")
    private Integer formType;
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


    @ApiModelProperty(value = "责任班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "责任人员名称")
    @TableField(exist = false)
    private String dutyUserName;

    @ApiModelProperty(value = "所属车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "关联工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "整改附件列表")
    @TableField(exist = false)
    private List<BuWorkRectifyAnnex> annexList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
