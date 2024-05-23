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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工单附件
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderAnnex对象", description = "工单附件")
@TableName("bu_work_order_annex")
public class BuWorkOrderAnnex extends Model<BuWorkOrderAnnex> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String workOrderId;

    @ApiModelProperty(value = "工单任务id")
    private String taskId;

    @ApiModelProperty(value = "这里为minio中存放文件的id或标识，没有则不需要提供")
    private String fileId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "文件类型 存放小写的文件扩展名称,如：.pdf")
    private String type;

    @ApiModelProperty(value = "文件路径 文件存放的路径，这里存放的是相对路径（包括存放在minio上也是相对路径）")
    private String savepath;

    @ApiModelProperty(value = "文件大小 单位为KB")
    private Long fileSize;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String workOrderName;

    @ApiModelProperty(value = "工单任务名称")
    @TableField(exist = false)
    private String taskName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
