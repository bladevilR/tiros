package org.jeecg.modules.outsource.bean;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 支付记录
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuContractPay对象", description = "支付记录")
@TableName("bu_contract_pay")
public class BuContractPay extends Model<BuContractPay> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "合同ID", required = true)
    @NotBlank
    private String contractId;

    @ApiModelProperty(value = "序号，自动累计")
    private Integer payNo;

    @ApiModelProperty(value = "支付描述")
    private String payDesc;
    @ApiModelProperty(value = "支付税率")
    private Double executeTaxRate;
    @ApiModelProperty(value = "支付金额", required = true)
    @NotNull
    private BigDecimal amount;
    @ApiModelProperty(value = "开票金额")
    private BigDecimal ticketAmount;
    @ApiModelProperty(value = "质保金比率")
    private Float qaRate;
    @ApiModelProperty(value = "质保金额")
    private BigDecimal qaMoney;

    @ApiModelProperty(value = "办理人")
    private String dealMan;

    @ApiModelProperty(value = "办理日期 yyyy-MM-dd")
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dealDate;

    @ApiModelProperty(value = "支付日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date payDate;

    @ApiModelProperty(value = "剩余支付")
    private BigDecimal leftover;
    @ApiModelProperty(value = "剩余质保金额")
    private BigDecimal leftQaMoney;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "合同名称")
    @TableField(exist = false)
    private String contractName;

    @ApiModelProperty(value = "合同编码")
    @TableField(exist = false)
    private String contractNo;

    @ApiModelProperty(value = "厂商")
    @TableField(exist = false)
    private String supplier;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

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
