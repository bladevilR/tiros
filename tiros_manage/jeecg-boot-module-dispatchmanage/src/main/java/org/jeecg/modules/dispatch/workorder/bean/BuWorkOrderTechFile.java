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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 任务的工艺文件除了在这个表中找，还默认去列计划中取相应的工艺文件，合并后去重
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderTechFile对象", description = "任务的工艺文件除了在这个表中找，还默认去列计划中取相应的工艺文件，合并后去重")
@TableName("bu_work_order_tech_file")
public class BuWorkOrderTechFile extends Model<BuWorkOrderTechFile> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属工单id")
    private String orderId;

    @ApiModelProperty(value = "所属工单任务id")
    private String orderTaskId;

    @ApiModelProperty(value = "文件表中文件ID,多个用英文逗号隔开")
    @NotBlank
    private String fileId;


    @ApiModelProperty(value = "文件名")
    @TableField(exist = false)
    private String fileName;

    @ApiModelProperty(value = "文件路径")
    @TableField(exist = false)
    private String savePath;

    @ApiModelProperty(value = "文件类型(扩展名)")
    @TableField(exist = false)
    private String fileType;

    @ApiModelProperty(value = "文件大小")
    @TableField(exist = false)
    private String fileSize;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
