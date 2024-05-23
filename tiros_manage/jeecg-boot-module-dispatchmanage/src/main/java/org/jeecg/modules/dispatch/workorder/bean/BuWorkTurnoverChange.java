package org.jeecg.modules.dispatch.workorder.bean;

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
 * 周转件更换，如果换上件没有记录，应先建立记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "周转件更换对象", description = "周转件更换，如果换上件没有记录，应先建立记录")
@TableName("bu_work_turnover_change")
public class BuWorkTurnoverChange extends Model<BuWorkTurnoverChange> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属线路id  字典dictCode=(bu_mtr_line,line_name,line_id)", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "车辆号  字典dictCode=(bu_train_info,train_no,train_no,line_id={所属线路id}", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "所属工单id  字典dictCode=(bu_work_order,order_name,id)", required = true)
    @NotBlank
    private String orderId;

    @ApiModelProperty(value = "换下部件id", required = true)
    @NotBlank
    private String downTurnoverId;

    @ApiModelProperty(value = "换上部件id  字典dictCode=(bu_material_spare_part,name,id,status=2 and system_id='{所属系统id}')", required = true)
    @NotBlank
    private String upTurnoverId;

    @ApiModelProperty(value = "更换班组id  字典dictCode=(bu_mtr_workshop_group,group_name,id)", required = true)
    @NotBlank
    private String groupId;

    @ApiModelProperty(value = "更换人员id  接口获取/tiros/sys/user/listByGroupId  app端接口/tiros/app/user/listByGroupId")
    private String userId;

    @ApiModelProperty(value = "更换类型 字典dictCode=bu_work_change_type", notes = "1换上换下 2拆分 3交换")
    @Dict(dicCode = "bu_work_change_type")
    private Integer changeType;

    @ApiModelProperty(value = "更换阶段 1架修期2质保期3其他  字典dictCode=bu_work_change_phase", required = true)
    @NotBlank
    @Dict(dicCode = "bu_work_change_phase")
    private Integer phase;

    @ApiModelProperty(value = "更换原因 0工艺必换件1故障更换2其他  字典dictCode=bu_work_change_reason", required = true)
    @NotBlank
    @Dict(dicCode = "bu_work_change_reason")
    private Integer reason;

    @ApiModelProperty(value = "原因描述")
    private String reasonDesc;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "更换部件名称")
    @TableField(exist = false)
    private String downTurnoverName;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

//    @ApiModelProperty(value = "所属系统名称")
//    @TableField(exist = false)
//    private String systemName;

    @ApiModelProperty(value = "原件序列号")
    @TableField(exist = false)
    private String downTurnoverManufNo;

    @ApiModelProperty(value = "原件安装位置")
    @TableField(exist = false)
    private String downTurnoverPlaceName;

    @ApiModelProperty(value = "新装件序列号")
    @TableField(exist = false)
    private String upTurnoverManufNo;

    @ApiModelProperty(value = "新装件原车号")
    @TableField(exist = false)
    private String upTurnoverTrainNo;

    @ApiModelProperty(value = "新装件原位置")
    @TableField(exist = false)
    private String upTurnoverPlaceName;

    @ApiModelProperty(value = "更换人员名称")
    @TableField(exist = false)
    private String userName;

    @ApiModelProperty(value = "列计划任务必换件id")
    @TableField(exist = false)
    private String mustReplaceId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
