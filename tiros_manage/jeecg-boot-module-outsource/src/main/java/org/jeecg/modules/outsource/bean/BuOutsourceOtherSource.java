package org.jeecg.modules.outsource.bean;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 委外文档资料，设备质量保证书、测试报告、监修日志、过程记录、质量报告
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuOutsourceOtherSource对象", description = "委外文档资料，设备质量保证书、测试报告、监修日志、过程记录、质量报告")
@TableName("bu_outsource_other_source")
public class BuOutsourceOtherSource extends Model<BuOutsourceOtherSource> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "交接明细")
    private String outinDetailId;

    @ApiModelProperty(value = "文件类型 字典bu_outin_other_file_type")
    private Integer fileType;
    @ApiModelProperty(value = "文件名字",required = true)
    private String name;
    @ApiModelProperty(value = "上传日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date uploadDate;

    @ApiModelProperty(value = "单位KB")
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
    private  String fileId;
    @ApiModelProperty(value = "文件类型", required = true)
    private String type;
    @ApiModelProperty(value = "文件路径", required = true)
    private String savepath;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
