package org.jeecg.modules.basemanage.regu.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 规程工艺文件,作业分类关联的文件会被下面的作业项继承
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("bu_repair_regu_tech_file")
public class BuRepairReguTechFile extends Model<BuRepairReguTechFile> {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "规程明细id")
    private String reguDetailId;

    @ApiModelProperty(value = "文件id,多个用英文逗号隔开")
    @NotBlank
    private String fileId;


    @ApiModelProperty(value = "文件名路径")
    @TableField(exist = false)
    private String savepath;

    @ApiModelProperty(value = "文件名")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "文件类型")
    @TableField(exist = false)
    private String type;

    @ApiModelProperty(value = "文件大小")
    @TableField(exist = false)
    private Long fileSize;

    @ApiModelProperty(value = "文件上传日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date uploadDate;

    @ApiModelProperty(value = "文件备注")
    @TableField(exist = false)
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
