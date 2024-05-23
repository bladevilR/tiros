package org.jeecg.modules.board.cost.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 特种设备运用/保养计划
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuSpecAssetPlan对象", description = "特种设备运用/保养计划")
public class BuSpecAssetPlan extends Model<BuSpecAssetPlan> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "特种设备id")
    private String specAssetId;

    @ApiModelProperty(value = "计划类型 1运用计划2保养计划")
    private Integer planType;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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


    @ApiModelProperty(value = "名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "编码")
    @TableField(exist = false)
    private String assetCode;

    @ApiModelProperty(value = "状态  字典dictCode=bu_tools_status")
    @Dict(dicCode = "bu_tools_status")
    @TableField(exist = false)
    private Integer status;

    @ApiModelProperty(value = "线路id")
    @TableField(exist = false)
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "车间id")
    @TableField(exist = false)
    private String workshopId;

    @ApiModelProperty(value = "车间名称")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "出厂编码")
    @TableField(exist = false)
    private String manufNo;

    @ApiModelProperty(value = "出厂日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date leaveFactory;

    @ApiModelProperty(value = "投入日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date useDate;

    @ApiModelProperty(value = "所属工班id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "所属工班名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "责任人员id")
    @TableField(exist = false)
    private String dutyUserId;

    @ApiModelProperty(value = "责任人员名称")
    @TableField(exist = false)
    private String dutyUserName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
