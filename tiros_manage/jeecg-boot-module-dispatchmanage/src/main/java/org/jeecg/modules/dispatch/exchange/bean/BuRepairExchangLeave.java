package org.jeecg.modules.dispatch.exchange.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 交接车开口项（遗留问题）
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "交接车开口项对象", description = "交接车开口项（遗留问题）")
@TableName("bu_repair_exchang_leave")
public class BuRepairExchangLeave extends Model<BuRepairExchangLeave> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属交接车id", required = true)
    @NotBlank
    private String exchangeId;

    @ApiModelProperty(value = "暂缓项内容")
    private String leaveContent;

    @ApiModelProperty(value = "所属系统id")
    private String assetTypeId;

    @ApiModelProperty(value = "作业项目（工序名称）")
    private String operation;

    @ApiModelProperty(value = "暂缓原因")
    private String reason;

    @ApiModelProperty(value = "计划处理措施")
    private String planHandle;

    @ApiModelProperty(value = "开口项状态 0未处理 1已处理  字典dictCode=bu_repair_leave_status")
    @Dict(dicCode = "bu_repair_leave_status")
    private Integer handleStatus;

    @ApiModelProperty(value = "最后一次处理措施")
    private String lastHandle;

    @ApiModelProperty(value = "故障id 如果是故障作为开口项则需要将故障id保存")
    private String faultId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "故障所属系统名称")
    @TableField(exist = false)
    private String assetTypeName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
