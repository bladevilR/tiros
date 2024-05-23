package org.jeecg.modules.board.cost.bean;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 支付记录
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-09
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

    @ApiModelProperty(value = "合同id")
    private String contractId;

    @ApiModelProperty(value = "支付序号 自动累计")
    private Integer payNo;

    @ApiModelProperty(value = "支付描述")
    private String payDesc;

    @ApiModelProperty(value = "支付税率 自动带入合同信息中的执行税率")
    private Double executeTaxRate;

    @ApiModelProperty(value = "开票金额")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "质保金比率 返还的质保金额比例")
    private Float qaRate;

    @ApiModelProperty(value = "质保金额 返还的质保金额=剩余质保金*质保金额比率")
    private BigDecimal qaMoney;

    @ApiModelProperty(value = "办理人员")
    private String dealMan;

    @ApiModelProperty(value = "办理日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dealDate;

    @ApiModelProperty(value = "支付日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date payDate;

    @ApiModelProperty(value = "剩余支付 如果税率发生变化，能自动根据税率重新计算剩余支付金额, 先计算出：剩余本金= 剩余金额/(1+上次税率), 剩余支付额= 剩余本金*(1+新的税率） ")
    private BigDecimal leftover;

    @ApiModelProperty(value = "剩余质保金额")
    private BigDecimal leftQaMoney;

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


    @ApiModelProperty(value = "修程id")
    @TableField(exist = false)
    private String repairProgramId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
