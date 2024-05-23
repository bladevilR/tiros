package org.jeecg.modules.dispatch.workorder.bean;

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
 * 工单任务的作业指导书
 * ：为对应列计划的任务的作业指导书+ 工单任务本身的
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderBookStep对象", description = "工单任务的作业指导书")
@TableName("bu_work_order_book_step")
public class BuWorkOrderBookStep extends Model<BuWorkOrderBookStep> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String workOrderId;

    @ApiModelProperty(value = "工单任务id")
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

    @ApiModelProperty(value = "是否属于工单任务 1是（表示属于工单任务） 0否（表示属于列计划任务/规程）")
    @TableField(exist = false)
    private Integer belongOrderTask;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
