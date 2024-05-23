package org.jeecg.modules.dispatch.serialplan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 作业人员需求
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairTaskStaffRequire对象", description = "作业人员需求")
@TableName("bu_repair_task_staff_require")
public class BuRepairTaskStaffRequire extends Model<BuRepairTaskStaffRequire> {

    @JsonIgnore
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
