package org.jeecg.modules.dispatch.workorder.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author youGen
 * @since 2020-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BuWorkPartChange对象", description="")
@TableName("bu_work_part_change")
public class BuWorkPartChange extends Model<BuWorkPartChange> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属工单id")
    private String orderId;

    @ApiModelProperty(value = "所属线路id")
    @NotBlank
    private String lineId;
    @ApiModelProperty(value = "车辆id")
    @NotBlank
    @TableField(exist = false)
    private String trainId;
    @ApiModelProperty(value = "车辆号 ")
    private String trainNo;
    @ApiModelProperty(value = "所属系统id")
    @NotBlank
    private String systemId;

    @ApiModelProperty(value = "位置(设备类型id）")
    @NotBlank
    private String assetTypeId;

    @ApiModelProperty(value = "更换件（物资类型id）")
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "更换数量")
    private Integer amount;

    @ApiModelProperty(value = "更换班组id")
    @NotBlank
    private String groupId;

    @ApiModelProperty(value = "更换人员id")
    private String userId;

    @ApiModelProperty(value = "更换阶段 1架修期2质保期3其他")
    @NotNull
    private Integer phase;

    @ApiModelProperty(value = "更换件序列号")
    private String downSn;

    @ApiModelProperty(value = "新件序列号")
    private String upSn;

    @ApiModelProperty(value = "更换日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date changeDate;

    @ApiModelProperty(value = "更换原因 1工艺必换2故障更换3其他")
    @NotNull
    private Integer reason;

    @ApiModelProperty(value = "责任人员 直接输入名字")
    private String dutyUser;

    @ApiModelProperty(value = "选择故障id 如果更换原因是故障更换的，需要选择关联故障")
    private String faultId;

    @ApiModelProperty(value = "原因描述")
    private String reasonDesc;
    @JsonIgnore
    private Date createTime;
    @ApiModelProperty(value = "创建人员")
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @ApiModelProperty(value = "修改人员")
    private String updateBy;
    @ApiModelProperty(value = "部件id")
    @TableField(exist = false)
    @NotBlank
    private String ReplacePartId;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
