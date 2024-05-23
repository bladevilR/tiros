package org.jeecg.modules.report.daily.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 委外出入库明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuOutsourceOutinDetail对象", description = "委外出入库明细")
@TableName("bu_outsource_outin_detail")
public class BuOutsourceOutinDetail extends Model<BuOutsourceOutinDetail> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "出入库单Id")
    private String outinId;

    @ApiModelProperty(value = "部件编号")
    private String assetNo;

    @ApiModelProperty(value = "部件名称")
    private String assetName;

    @ApiModelProperty(value = "部件数量")
    private Integer amount;

    @ApiModelProperty(value = "部件类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "周转件表id")
    private String turnoverAssetId;

    @ApiModelProperty(value = "外观状态  字典dicCode=bu_facade_status")
    @Dict(dicCode = "bu_facade_status")
    private Integer facadeStatus;

    @ApiModelProperty(value = "外观描述")
    private String facadeDesc;

    @ApiModelProperty(value = "工装状态  字典dicCode=bu_facade_status")
    @Dict(dicCode = "bu_facade_status")
    private Integer mixtoolStatus;

    @ApiModelProperty(value = "工装描述")
    private String mixtoolDesc;

    @ApiModelProperty(value = "是否存在故障 字典dicCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer fault;

    @ApiModelProperty(value = "是否返厂 字典dicCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer returnStatus;

    @ApiModelProperty(value = "根据合同中规定的天数自动推算")
    private Date returnTime;

    @ApiModelProperty(value = "设置的逾期返厂原因")
    private String delayReason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "出库单明细id")
    private String outDetailId;


    @ApiModelProperty(value = "类型  字典dicCode=bu_bill_type   0出库单1入库单")
    @TableField(exist = false)
    @Dict(dicCode = "bu_bill_type")
    private Integer billType;

    @ApiModelProperty(value = "移交日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date transferDate;

    @ApiModelProperty(value = "部件类型名称")
    @TableField(exist = false)
    private String assetTypeName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
