package org.jeecg.modules.basemanage.qualityvisual.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("bu_quality_visual")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "质量可视化对象", description = "质量可视化信息表")
public class BuQualityVisual implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private String id;

    @Excel(name = "列计划ID", width = 20)
    @ApiModelProperty(value = "关联列计划ID")
    private String planId;

    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private String trainNo;

    @Excel(name = "车型", width = 15)
    @ApiModelProperty(value = "车型")
    private String trainType;

    @Excel(name = "项目名称", width = 20)
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "工序流程数据JSON")
    private String processFlow;

    @ApiModelProperty(value = "质量检查点数据JSON")
    private String qualityPoints;

    @ApiModelProperty(value = "已完成工序数")
    private Integer completedProcesses;

    @ApiModelProperty(value = "总工序数")
    private Integer totalProcesses;

    @Excel(name = "完成进度", width = 15)
    @ApiModelProperty(value = "完成进度百分比")
    private String progress;

    @ApiModelProperty(value = "质量问题数")
    private Integer qualityIssues;

    @ApiModelProperty(value = "未关闭问题数")
    private Integer openIssues;

    @ApiModelProperty(value = "质量等级：A-优秀 B-良好 C-合格 D-不合格")
    private String qualityLevel;

    @Excel(name = "备注", width = 30)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除标志：0-未删除 1-已删除")
    private Integer delFlag;
}
