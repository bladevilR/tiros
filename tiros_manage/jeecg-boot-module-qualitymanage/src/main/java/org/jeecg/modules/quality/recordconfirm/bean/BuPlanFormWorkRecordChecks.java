package org.jeecg.modules.quality.recordconfirm.bean;

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

/**
 * <p>
 * 列计划作业记录检查结果
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuPlanFormWorkRecordChecks对象", description = "列计划作业记录检查结果")
@TableName("bu_pl_work_record_result")
public class BuPlanFormWorkRecordChecks extends Model<BuPlanFormWorkRecordChecks> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "记录明细结果id 来自列计划作业记录表明细结果表id", required = true)
    @NotBlank
    private String recordDetailId;

    @ApiModelProperty(value = "检查类型  字典dictCode=bu_work_order_record_check_type", notes = "1自检2互检3专检4抽检5过程检6过程检确认7交接检8交接检确认", required = true)
    @NotBlank
    @Dict(dicCode = "bu_work_order_record_check_type")
    private Integer checkType;

    @ApiModelProperty(value = "检查人员id", required = true)
    @NotBlank
    private String checkUserId;

    @ApiModelProperty(value = "检查时间 yyyy-MM-dd HH:mm:ss", required = true)
    @NotBlank
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    @ApiModelProperty(value = "检查结果  字典dicCode=bu_work_order_record_check_result", notes = "1正常0存在问题")
    @Dict(dicCode = "bu_work_order_record_check_result")
    private Integer result;

    @ApiModelProperty(value = "结果描述")
    private String resultDesc;

    @ApiModelProperty(value = "第几列")
    private  Integer colNum;

    @ApiModelProperty(value = "同检人员ids")
    private String togetherCheckUserIds;

    @ApiModelProperty(value = "同检人员名称s")
    private String togetherCheckUserNames;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "检查人名称")
    @TableField(exist = false)
    private String checkUserName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
