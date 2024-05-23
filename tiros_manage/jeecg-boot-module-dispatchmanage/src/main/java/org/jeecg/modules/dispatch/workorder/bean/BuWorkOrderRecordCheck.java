package org.jeecg.modules.dispatch.workorder.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 如果是专检时，则需要将检查结果填写到明细表
 * </p>
 *
 * @author youGen
 * @since 2020-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderRecordCheck对象", description = "如果是专检时，则需要将检查结果填写到明细表")
@TableName("bu_work_order_record_check")
public class BuWorkOrderRecordCheck extends Model<BuWorkOrderRecordCheck> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "作业记录明细id")
    private String recordDetailId;

    @ApiModelProperty(value = "检查类型 1自检2互检3专检4抽检5过程检6过程检确认7交接检8交接检确认  字典dictCode=bu_work_order_record_check_type")
    @Dict(dicCode = "bu_work_order_record_check_type")
    private Integer checkType;

    @ApiModelProperty(value = "检查人id")
    private String checkUserId;

    @ApiModelProperty(value = "检查时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkTime;

    @ApiModelProperty(value = "检查结果 1正常0存在问题  字典dicCode=bu_work_order_record_check_result")
    @Dict(dicCode = "bu_work_order_record_check_result")
    private Integer result;

    @ApiModelProperty(value = "结果描述")
    private String resultDesc;

    @ApiModelProperty(value = "检查人名称")
    @TableField(exist = false)
    private String checkUserName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
