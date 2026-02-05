package org.jeecg.modules.basemanage.quotabom.entity;

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
@TableName("bu_quota_bom")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "定额BOM对象", description = "定额BOM信息表")
public class BuQuotaBom implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private String id;

    @Excel(name = "BOM编码", width = 15)
    @ApiModelProperty(value = "BOM编码")
    private String bomCode;

    @Excel(name = "BOM名称", width = 20)
    @ApiModelProperty(value = "BOM名称")
    private String bomName;

    @Excel(name = "车型", width = 15)
    @ApiModelProperty(value = "适用车型")
    private String trainType;

    @Excel(name = "线别", width = 15)
    @ApiModelProperty(value = "线别")
    private String line;

    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    private String position;

    @ApiModelProperty(value = "所属系统")
    private String system;

    @ApiModelProperty(value = "二级模块")
    private String moduleLevel2;

    @ApiModelProperty(value = "三级模块")
    private String moduleLevel3;

    @ApiModelProperty(value = "部件明细JSON数据")
    private String partDetails;

    @ApiModelProperty(value = "爆炸图URL")
    private String explosionDiagram;

    @ApiModelProperty(value = "关联物资JSON数据")
    private String materialLinks;

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
