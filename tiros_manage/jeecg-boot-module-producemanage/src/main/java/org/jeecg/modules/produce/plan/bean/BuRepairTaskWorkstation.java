package org.jeecg.modules.produce.plan.bean;

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
 * 任务工位
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairTaskWorkstation对象", description = "任务工位")
@TableName("bu_repair_task_workstation")
public class BuRepairTaskWorkstation extends Model<BuRepairTaskWorkstation> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "工位id")
    private String workstationId;

    @ApiModelProperty(value = "工位--所属车间id")
    @TableField(exist = false)
    private String workshopId;

    @ApiModelProperty(value = "工位--工位号")
    @TableField(exist = false)
    private String stationNo;

    @ApiModelProperty(value = "工位--名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "工位--位置")
    @TableField(exist = false)
    private String location;

    @ApiModelProperty(value = "工位--作业内容")
    @TableField(exist = false)
    private String content;

    @ApiModelProperty(value = "工位--备注")
    @TableField(exist = false)
    private String remark;

    @ApiModelProperty(value = "工位--所属车间名称")
    @TableField(exist = false)
    private String workshopName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
