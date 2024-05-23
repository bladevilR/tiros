package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_announcement")
public class SysAnnouncement implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    @Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
    private String titile;

    @Excel(name = "内容", width = 30)
    @ApiModelProperty(value = "内容")
    private String msgContent;

    @Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Excel(name = "发布人", width = 15)
    @ApiModelProperty(value = "发布人")
    private String sender;

    @Excel(name = "优先级", width = 15, dicCode = "priority")
    @ApiModelProperty(value = "优先级 字典dictCode=priority", notes = "L低 M中 H高")
    @Dict(dicCode = "priority")
    private String priority;


    @Excel(name = "消息类型", width = 15, dicCode = "msg_category")
    @ApiModelProperty(value = "消息类型 字典dictCode=msg_category", notes = "1通知公告 2系统消息")
    @Dict(dicCode = "msg_category")
    private String msgCategory;

    @Excel(name = "通告对象类型", width = 15, dicCode = "msg_type")
    @ApiModelProperty(value = "通告对象类型 字典dictCode=msg_type", notes = "USER指定用户 ALL全体用户")
    @Dict(dicCode = "msg_type")
    private String msgType;

    @Excel(name = "发布状态", width = 15, dicCode = "send_status")
    @ApiModelProperty(value = "发布状态 字典dictCode=send_status", notes = "0未发布 1已发布 2已撤销")
    @Dict(dicCode = "send_status")
    private String sendStatus;

    @Excel(name = "发布时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发布时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    @Excel(name = "撤销时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "撤销时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    @ApiModelProperty(value = "是否删除 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private String delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "指定用户")
    private String userIds;

    @ApiModelProperty(value = "业务类型 email:邮件 bpm:流程")
    private String busType;

    @ApiModelProperty(value = "业务id")
    private String busId;

    @ApiModelProperty(value = "打开方式 组件：component 路由：url")
    private String openType;

    @ApiModelProperty(value = "组件/路由 地址")
    private String openPage;

    @ApiModelProperty(value = "摘要")
    private String msgAbstract;


    @ApiModelProperty(value = "是否已阅读 字典dictCode=bu_state")
    @TableField(exist = false)
    @Dict(dicCode = "bu_state")
    private String readFlag;

    @ApiModelProperty(value = "阅读时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date readTime;

    @Excel(name = "发布人名字", width = 15)
    @ApiModelProperty(value = "发布人名字")
    @TableField(exist = false)
    private String senderName;

}
