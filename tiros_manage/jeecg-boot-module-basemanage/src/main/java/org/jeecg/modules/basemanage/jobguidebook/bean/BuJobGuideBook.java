package org.jeecg.modules.basemanage.jobguidebook.bean;

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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuJobGuideBook对象", description = "作业指导书")
@TableName("bu_job_guide_book")
public class BuJobGuideBook extends Model<BuJobGuideBook> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "文件编号", required = true)
    @NotBlank
    private String fileNo;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank
    private String fileName;

    @ApiModelProperty(value = "版本号", required = true)
    @NotBlank
    private String fileVer;

    @ApiModelProperty(value = "所属线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "车型id")
    private String trainTypeId;

    @ApiModelProperty(value = "修程id", required = true)
    @NotBlank
    private String repairProgramId;

    @ApiModelProperty(value = "项目")
    private String project;

    @ApiModelProperty(value = "实施日期", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date exeTime;

    @ApiModelProperty(value = "状态：0-草稿 1-发布 2-审批中 3-审批通过 9-作废")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "是否模板：0-否 1-模板")
    private Integer templateFlag;

    @ApiModelProperty(value = "正文(HTML)")
    private String contentHtml;

    @ApiModelProperty(value = "审核状态：0-草稿 1-待审核 2-审核通过 3-审核驳回")
    private Integer reviewStatus;

    @ApiModelProperty(value = "审核人ID")
    private String reviewerId;

    @ApiModelProperty(value = "审核人姓名")
    private String reviewerName;

    @ApiModelProperty(value = "审核意见")
    private String reviewComment;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    @ApiModelProperty(value = "审批人ID")
    private String approverId;

    @ApiModelProperty(value = "审批人姓名")
    private String approverName;

    @ApiModelProperty(value = "审批意见")
    private String approveComment;

    @ApiModelProperty(value = "审批时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

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

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "编制人")
    @TableField(exist = false)
    private String creatorName;

    @ApiModelProperty(value = "车型名称")
    @TableField(exist = false)
    private String trainTypeName;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProgramName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
