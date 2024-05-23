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
 * 记录表数据值记录：如果数据量大，查询慢，则把预警为1的放入内存表，修正后更新这边对应记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPlanFormValues对象", description = "记录表数据值记录：如果数据量大，查询慢，则把预警为1的放入内存表，修正后更新这边对应记录")
@TableName("bu_pl_data_record_value")
public class BuPlanFormValues extends Model<BuPlanFormValues> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属列计划表单id", required = true)
    @NotBlank
    private String planFormId;

    @ApiModelProperty(value = "所属记录表结果id", required = true)
    @NotBlank
    private String formDataRecordId;

    @ApiModelProperty(value = "在线表单(数据记录表)id", required = true)
    @NotBlank
    private String formObjId;

    @ApiModelProperty(value = "测量项id 来自bu_work_measure_item表", required = true)
    @NotBlank
    private String measureThresholdId;

    @ApiModelProperty(value = "预警值 当前测量的值")
    private Double alertValue;

    @ApiModelProperty(value = "是否预警 0否1是， 如果当前值超出设定的阈值范围，则为1")
    @Dict(dicCode = "bu_state")
    private Integer alertHappen;

    @ApiModelProperty(value = "预警信息 是否预警为1时，这是根据测量项的预警信息模版生成，并替换中间的占位符")
    private String alertMessage;

    @ApiModelProperty(value = "处理状态 0未关闭1已关闭2已转故障")
    @Dict(dicCode = "bu_work_measure_alert_status")
    private Integer status;

    @ApiModelProperty(value = "修正值")
    private Double fixedValue;

    @ApiModelProperty(value = "作业班组id", required = true)
    @NotBlank
    private String groupId;

    @ApiModelProperty(value = "预警时间 yyyy-MM-dd HH:mm:ss", required = true)
    @NotBlank
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alertTime;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "修复备注")
    private String fixDesc;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "数据值位置 对应着自定义表单中的数据输入项，如果是excel表单，则是行列表示，如：$1$1 表第1行第1列，这里只能是单个单元格，不能是范围")
    private String linkDomain;

    @ApiModelProperty(value = "阈值 当前的阈值")
    private Double thresholdValue;


    @ApiModelProperty(value = "表单对象名称")
    @TableField(exist = false)
    private String formObjName;

    @ApiModelProperty(value = "作业班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "作业工位名称，sql查询以列表形式返回，再转成workstationName返回前端")
    @TableField(exist = false)
    @JsonIgnore
    private List<String> workstationNameList;

    @ApiModelProperty(value = "作业工位名称")
    @TableField(exist = false)
    private String workstationNames;

    @ApiModelProperty(value = "作业工序")
    @TableField(exist = false)
    private String taskName;

    @ApiModelProperty(value = "阈值操作符 字典dictCode=bu_operator")
    @TableField(exist = false)
    @Dict(dicCode = "bu_operator")
    private Integer thresholdOperator;

    @ApiModelProperty(value = "阈值 当前的阈值")
    @TableField(exist = false)
    private Double thresholdValue2;

    @ApiModelProperty(value = "阈值操作符 字典dictCode=bu_operator")
    @TableField(exist = false)
    @Dict(dicCode = "bu_operator")
    private Integer thresholdOperator2;

    @ApiModelProperty(value = "阈值描述")
    @TableField(exist = false)
    private String thresholdValueDesc;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
