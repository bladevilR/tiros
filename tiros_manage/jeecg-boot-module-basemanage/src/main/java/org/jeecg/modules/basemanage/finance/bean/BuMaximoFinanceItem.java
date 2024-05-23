package org.jeecg.modules.basemanage.finance.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 财务项目
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_maximo_finance_item")
@ApiModel(value = "BuMaximoFinanceItem对象", description = "财务项目")
public class BuMaximoFinanceItem extends Model<BuMaximoFinanceItem> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "类型 PROJECT项目 TASK任务")
    private String type;

    @ApiModelProperty(value = "编号")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "财务编号")
    private String fcCode;

    @ApiModelProperty(value = "上级财务编号")
    private String parentFcCode;

    @ApiModelProperty(value = "财务编号wbs")
    private String wbs;

    @ApiModelProperty(value = "所属项目编号")
    private String projectCode;

    @ApiModelProperty(value = "任务级别")
    @TableField("TASK_LEVEL")
    private Integer taskLevel;

    @ApiModelProperty(value = "当前状态")
    private String fcStatus;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "maximo中orgid")
    private String maximoOrgId;

    @ApiModelProperty(value = "maximo中siteid")
    private String maximoSiteId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
