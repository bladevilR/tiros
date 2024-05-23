package org.jeecg.modules.third.jdx.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 故障信息处理记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "故障信息处理记录对象", description = "故障信息处理记录")
@TableName("bu_fault_handle_record")
public class BuFaultHandleRecord extends Model<BuFaultHandleRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "故障信息id", required = true)
    @NotBlank
    private String faultInfoId;

    @ApiModelProperty(value = "故障原因id  接口获取/tiros/dispatch/fault/reason/list")
    private String faultReasonId;

    @ApiModelProperty(value = "故障原因描述", required = true)
    @NotBlank
    private String reasonDesc;

    @ApiModelProperty(value = "处理措施id  接口获取/tiros/dispatch/fault/solution/list")
    private String faultSolutionId;

    @ApiModelProperty(value = "处理措施描述", required = true)
    @NotBlank
    private String solutionDesc;

    @ApiModelProperty(value = "处理时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date solutionTime;

    @ApiModelProperty(value = "处理班组id  字典dictCode=(bu_mtr_workshop_group,group_name,id)", required = true)
    @NotBlank
    private String solutionGroupId;

    @ApiModelProperty(value = "处理人员id  接口获取/tiros/sys/user/listByGroupId  app端接口/tiros/app/user/listByGroupId", required = true)
    @NotBlank
    private String solutionUserId;

    @ApiModelProperty(value = "处理结果 0未解决 1已解决   字典dictCode=bu_fault_handle_result")
//    @Dict(dicCode = "bu_fault_handle_result")
    private Integer result;


    @ApiModelProperty(value = "故障原因编码")
    @TableField(exist = false)
    private String faultReasonCode;

    @ApiModelProperty(value = "处理措施编码")
    @TableField(exist = false)
    private String faultSolutionCode;

    @ApiModelProperty(value = "处理班组名称")
    @TableField(exist = false)
    private String solutionGroupName;

    @ApiModelProperty(value = "处理人员名称")
    @TableField(exist = false)
    private String solutionUserName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
