package org.jeecg.modules.third.jdx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 线路
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_mtr_line")
public class BuMtrLine extends Model<BuMtrLine> {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String lineId;

    @ApiModelProperty(value = "车型id")
    private String trainTypeId;

    @ApiModelProperty(value = "线路编号")
    private String lineNum;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "线路长度")
    private BigDecimal lineLength;

    @ApiModelProperty(value = "天平均里程数")
    private BigDecimal dayKilometer;

    @ApiModelProperty(value = "开通时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date beginTime;

    @ApiModelProperty(value = "站点数")
    private Integer stationAmount;

    @ApiModelProperty(value = "运营开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date runStart;

    @ApiModelProperty(value = "运营结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date runEnd;

    @ApiModelProperty(value = "运营公司id")
    private String companyId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "车型名称")
    @TableField(exist = false)
    private String trainTypeName;

    @ApiModelProperty(value = "运营公司名称")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value = "车辆段id")
    @TableField(exist = false)
    private String depotId;


    @Override
    protected Serializable pkVal() {
        return this.lineId;
    }

}
