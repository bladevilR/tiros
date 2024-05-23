package org.jeecg.modules.third.jdx.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车辆信息
 * 车辆结构，平时在界面显示时从车辆结构中查数据，只有在保存业务数据要用到具体的设备时，才用对应的：结构id+车辆ID去查到
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTrainInfo对象", description = "车辆信息")
@TableName("bu_train_info")
public class BuTrainInfo extends Model<BuTrainInfo> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "车型id")
    private String trainTypeId;

    @ApiModelProperty(value = "所属线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "车辆编号  长度限制16", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "编组数量  大小限制4位整数")
    @NotNull
    private Integer groupNum;

    @ApiModelProperty(value = "走行里程 千米  大小限制18位小数，小数点后2位", required = true)
    @NotNull
    private Double mileage;

    @ApiModelProperty(value = "车辆状态 0未上线 1已上线  字典dictCode=bu_train_status")
    @NotNull
//    @Dict(dicCode = "bu_train_status")
    private Integer status;

    @ApiModelProperty(value = "投入运营时间 yyyy-MM-dd", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date useDate;

    @ApiModelProperty(value = "车辆的质保结束日期 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warrantyDate;

    @ApiModelProperty(value = "当前停放股道id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trackId;

    @ApiModelProperty(value = "所属厂商id")
    private String supplierId;

    @ApiModelProperty(value = "合同编号  长度限制32")
    private String contractNo;

    @ApiModelProperty(value = "合同名称  长度限制32")
    private String contractName;

    @ApiModelProperty(value = "制造日期 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date madeDate;

    @ApiModelProperty(value = "购入日期 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date buyDate;

    @ApiModelProperty(value = "车辆结构id")
    private String trainStructId;

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

    @ApiModelProperty(value = "是否已生成车辆设备 0否1是")
    private Integer isGenAsset;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "车型名称")
    @TableField(exist = false)
    private String trainType;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String line;

    @ApiModelProperty(value = "当前停放股道名称")
    @TableField(exist = false)
    private String track;

    @ApiModelProperty(value = "所属厂商名称")
    @TableField(exist = false)
    private String supplier;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
