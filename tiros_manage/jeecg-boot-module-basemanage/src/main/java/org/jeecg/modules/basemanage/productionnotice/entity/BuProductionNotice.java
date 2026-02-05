package org.jeecg.modules.basemanage.productionnotice.entity;

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

/**
 * 生产通知单
 */
@Data
@TableName("bu_production_notice")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "生产通知单对象", description = "生产通知单信息表")
public class BuProductionNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键ID")
    private String id;

    @Excel(name = "通知单号", width = 15)
    @ApiModelProperty(value = "通知单号")
    private String noticeNo;

    @Excel(name = "通知单标题", width = 20)
    @ApiModelProperty(value = "通知单标题")
    private String title;

    @Excel(name = "通知单类型", width = 15, dicCode = "production_notice_type")
    @ApiModelProperty(value = "通知单类型：1-技术类 2-变更类")
    private String noticeType;

    @Excel(name = "状态", width = 15, dicCode = "production_notice_status")
    @ApiModelProperty(value = "状态：0-草稿 1-审核中 2-已发布 3-已关闭")
    private String status;

    @Excel(name = "车型", width = 15)
    @ApiModelProperty(value = "适用车型")
    private String trainType;

    @Excel(name = "线别", width = 15)
    @ApiModelProperty(value = "适用线别")
    private String line;

    @Excel(name = "作业范围", width = 20)
    @ApiModelProperty(value = "作业范围描述")
    private String scope;

    @ApiModelProperty(value = "涉及列车总数")
    private Integer totalTrains;

    @ApiModelProperty(value = "已完成列车数")
    private Integer completedTrains;

    @Excel(name = "进度", width = 15)
    @ApiModelProperty(value = "完成进度百分比")
    private String progress;

    @ApiModelProperty(value = "通知内容")
    private String content;

    @ApiModelProperty(value = "关联工艺文件ID，多个逗号分隔")
    private String relatedDocIds;

    @ApiModelProperty(value = "关联作业记录表ID，多个逗号分隔")
    private String relatedFormIds;

    @Excel(name = "编制人", width = 15)
    @ApiModelProperty(value = "编制人")
    private String creator;

    @Excel(name = "核对人", width = 15)
    @ApiModelProperty(value = "核对人")
    private String checker;

    @Excel(name = "审批人", width = 15)
    @ApiModelProperty(value = "审批人")
    private String approver;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

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
