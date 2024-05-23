package org.jeecg.modules.group.partchange.bean;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 部件更换
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "部件更换对象", description = "部件更换")
@TableName("bu_work_part_change")
public class BuWorkPartChange extends Model<BuWorkPartChange> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属工单id  字典dictCode=(bu_work_order,order_name,id)")
    private String orderId;

    @ApiModelProperty(value = "所属线路id  字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "车辆号  字典dictCode=(bu_train_info,train_no,train_no,line_id={所属线路id}")
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "所属系统id  字典dictCode=(bu_train_asset_type,name,id,struct_type=1 and parent_id is null)")
    @NotBlank
    private String systemId;

    @ApiModelProperty(value = "位置(设备类型id）  接口获取/tiros/trainAssetType/list/Asset")
    @NotBlank
    private String assetTypeId;

    @ApiModelProperty(value = "更换件（物资类型id）")
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "更换数量")
    private Integer amount;

    @ApiModelProperty(value = "更换班组id  字典dictCode=(bu_mtr_workshop_group,group_name,id)")
    @NotBlank
    private String groupId;

    @ApiModelProperty(value = "更换人员id  接口获取/tiros/sys/user/listByGroupId  app端接口/tiros/app/user/listByGroupId")
    private String userId;

    @ApiModelProperty(value = "更换阶段 1架修期2质保期3其他  字典dictCode=bu_work_change_phase")
    @NotNull
    @Dict(dicCode = "bu_work_change_phase")
    private Integer phase;

    @ApiModelProperty(value = "更换件序列号")
    private String downSn;

    @ApiModelProperty(value = "新件序列号")
    private String upSn;

    @ApiModelProperty(value = "更换日期 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date changeDate;

    @ApiModelProperty(value = "更换原因 1工艺必换2故障更换3其他  字典dictCode=bu_work_change_reason")
    @NotNull
    @Dict(dicCode = "bu_work_change_reason")
    private Integer reason;

    @ApiModelProperty(value = "责任人员 直接输入名字")
    private String dutyUser;

    @ApiModelProperty(value = "选择故障id 如果更换原因是故障更换的，需要选择关联故障")
    private String faultId;

    @ApiModelProperty(value = "原因描述")
    private String reasonDesc;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    @ApiModelProperty(value = "更换部件名称（物资类型名称）")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "更换部件位置(设备类型名称)")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "更换人员名称")
    private String userName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
