package org.jeecg.modules.dispatch.serialplan.bean;

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
 * 列计划任务关联作业指导书明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairTaskBookStep对象", description = "在任务管理规程后，自动将规程的作业指导书步骤明细带入，注意去掉重复的")
@TableName("bu_repair_task_book_step")
public class BuRepairTaskBookStep extends Model<BuRepairTaskBookStep> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "列计划任务id")
    private String taskId;

    @ApiModelProperty(value = "作业指导书id")
    private String techBookId;

    @ApiModelProperty(value = "作业指导书明细id")
    private String bookDetailId;

    @ApiModelProperty(value = "作业指导书步骤序号")
    private Integer bookStepNo;

    @ApiModelProperty(value = "作业指导书步骤标题")
    private String bootStepTitle;


    @ApiModelProperty(value = "所属作业指导书编号")
    @TableField(exist = false)
    private String fileNo;

    @ApiModelProperty(value = "所属作业指导书")
    @TableField(exist = false)
    private String bookName;

    @ApiModelProperty(value = "作业指导书步骤内容")
    @TableField(exist = false)
    private String bootStepContent;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
