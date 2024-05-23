package org.jeecg.modules.quality.check.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 作业记录明细检查信息 如果是专检时，则需要将检查结果填写到明细表
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "作业记录明细检查信息对象", description = "如果是专检时，则需要将检查结果填写到明细表")
@TableName("bu_work_order_record_check")
public class BuWorkOrderRecordCheck extends Model<BuWorkOrderRecordCheck> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属记录明细id", required = true)
    @NotBlank
    private String recordDetailId;

    @ApiModelProperty(value = "检查类型 1自检2互检3专检4抽检5过程检6过程检确认7交接检8交接检确认", required = true)
    @NotNull
    private Integer checkType;

    @ApiModelProperty(value = "检查人员id", required = true)
    @NotBlank
    private String checkUserId;

    @ApiModelProperty(value = "检查时间")
    private Date checkTime;

    @ApiModelProperty(value = "检查结果 1正常0存在问题")
    private Integer result;

    @ApiModelProperty(value = "结果描述")
    private String resultDesc;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
