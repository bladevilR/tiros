package org.jeecg.modules.basemanage.techbook.bean;

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
import java.util.Date;

/**
 * <p>
 * 作业指导书(工艺)
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairTechBook对象", description = "作业指导书(工艺)")
@TableName("bu_repair_tech_book")
public class BuRepairTechBook extends Model<BuRepairTechBook> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "文件编号", required = true)
    @NotBlank
    private String fileNo;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank
    private String fileName;

    @ApiModelProperty(value = "文件版本", required = true)
    @NotBlank
    private String fileVer;

    @ApiModelProperty(value = "所属线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "所属修程id", required = true)
    @NotBlank
    private String repairProgramId;

    @ApiModelProperty(value = "实施日期", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date exeTime;

    @ApiModelProperty(value = "有效状态：1：有效，0：无效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "是否模板：0-否 1-模板")
    private Integer templateFlag;

    @ApiModelProperty(value = "正文(HTML)")
    private String contentHtml;

    @ApiModelProperty(value = "审阅状态：0-草稿 1-待审 2-通过 3-驳回")
    private Integer reviewStatus;

    @ApiModelProperty(value = "审阅人ID")
    private String reviewerId;

    @ApiModelProperty(value = "审阅人姓名")
    private String reviewerName;

    @ApiModelProperty(value = "审阅意见")
    private String reviewComment;

    @ApiModelProperty(value = "审阅时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属修程名称")
    @TableField(exist = false)
    private String repairProgramName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
