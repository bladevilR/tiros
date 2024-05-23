package org.jeecg.modules.basemanage.tpplan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 所需人员
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTpRepairPlanPerson对象", description = "所需人员")
@TableName("bu_tp_repair_plan_person")
public class BuTpRepairPlanPerson extends Model<BuTpRepairPlanPerson> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "所需人数", required = true)
    private Integer amount;

    @ApiModelProperty(value = "要求人员的岗位，可以多个岗位，逗号分隔")
    private String requirePostion;

    @ApiModelProperty(value = "证书要求")
    private String requireCertificate;

    @ApiModelProperty(value = "技能要求")
    private String requireTech;

    @ApiModelProperty("备注")
    private String remark;


    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;

    @ApiModelProperty(value = "工班id")
    @TableField(exist = false)
    private String workGroupId;

    @ApiModelProperty(value = "工班名称")
    @TableField(exist = false)
    private String workGroupName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
