package org.jeecg.modules.basemanage.standardprocess.entity;

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
@TableName("bu_standard_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "标准工序对象", description = "标准工序信息表")
public class BuStandardProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private String id;

    @Excel(name = "工序编号", width = 15)
    @ApiModelProperty(value = "工序编号")
    private String processNo;

    @Excel(name = "工序名称", width = 20)
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @Excel(name = "工序类型", width = 15, dicCode = "process_type")
    @ApiModelProperty(value = "工序类型：1-拆卸 2-检修 3-安装")
    private String processType;

    @ApiModelProperty(value = "工序步骤内容")
    private String processSteps;

    @ApiModelProperty(value = "标准工时（分钟）")
    private Integer standardDuration;

    @ApiModelProperty(value = "工序图片，多个逗号分隔")
    private String processImages;

    @ApiModelProperty(value = "紧固顺序配置")
    private String fastenSequence;

    @ApiModelProperty(value = "紧固示意图")
    private String fastenDiagram;

    @ApiModelProperty(value = "适用车型")
    private String trainType;

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
