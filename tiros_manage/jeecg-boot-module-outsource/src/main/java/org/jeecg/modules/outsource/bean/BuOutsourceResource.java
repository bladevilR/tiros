package org.jeecg.modules.outsource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 委外过程资料
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuOutsourceResource对象", description = "委外过程资料")
@TableName("bu_outsource_resource")
public class BuOutsourceResource extends Model<BuOutsourceResource> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "合同id", required = true)
    @NotBlank
    private String contractId;

    @ApiModelProperty(value = "1 开工审核情况 2 计划实现情况 3 质量情况 4 售后响应情况 ", required = true)
    @NotNull
    @Dict(dicCode = "bu_outin_file_type")
    private Integer fileType;
    @ApiModelProperty(value = "文件名", required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(value = "文件类型", required = true)
    private String type;
    @ApiModelProperty(value = "文件路径", required = true)
    private String savepath;
    @ApiModelProperty(value = "上传日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date uploadDate;

    @ApiModelProperty(value = "大小 单位KB")
    private BigDecimal fileSize;
    @ApiModelProperty(value = "备注")
    private String remark;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;
    private String fileId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
