package org.jeecg.modules.dispatch.toolplan.bean;

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
 * 工具(装)运用/保养计划
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "工装运用/保养计划对象", description = "工具(装)运用/保养计划")
@TableName("bu_tool_plan")
public class BuToolPlan extends Model<BuToolPlan> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工装id  字典dictCode=(bu_material_tools,name,id)", required = true)
    @NotBlank
    private String toolId;

    @ApiModelProperty(value = "计划类型 1运用计划2保养计划", required = true)
    @NotNull
    private Integer planType;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date endTime;

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

    @ApiModelProperty(value = "工装名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "工装规格")
    @TableField(exist = false)
    private String model;

    @ApiModelProperty(value = "工装状态 1正常 2维修 3报废 4送检 字典dictCode=bu_tools_status")
    @TableField(exist = false)
    @Dict(dicCode = "bu_tools_status")
    private Integer status;

    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "资产编码")
    @TableField(exist = false)
    private String assetCode;

    @ApiModelProperty(value = "入场使用日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date entraceDate;

    @ApiModelProperty(value = "库位(所属仓库名称)")
    @TableField(exist = false)
    private String warehouseName;

    @ApiModelProperty(value = "所属工班名称")
    @TableField(exist = false)
    private String groupName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
