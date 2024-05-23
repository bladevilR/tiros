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
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 合同信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuContractInfo对象", description = "合同信息")
@TableName("bu_contract_info")
public class BuContractInfo extends Model<BuContractInfo> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "合同编号", required = true)
    @NotBlank
    private String contractNo;

    @ApiModelProperty(value = "合同名称", required = true)
    @NotBlank
    private String contractName;

    @ApiModelProperty(value = "合同厂商id")
    private String supplierId;

    @ApiModelProperty(value = "合同金额", required = true)
    @NotNull
    private BigDecimal amount;

    @ApiModelProperty(value = "合同税率", required = true)
    @NotNull
    private Float taxRate;

    @ApiModelProperty(value = "合同状态 字典dictCode=bu_contract_status", required = true)
    @Dict(dicCode = "bu_contract_status")
    @NotNull
    private Integer status;

    @ApiModelProperty(value = "合同开始日期 yyyy-MM-dd", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date startDate;

    @ApiModelProperty(value = "合同结束日期 yyyy-MM-dd", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date finishDate;

    @ApiModelProperty(value = "签订日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date signDate;

    @ApiModelProperty(value = "保证金额")
    private BigDecimal deposit;

    @ApiModelProperty(value = "暂列金额")
    private BigDecimal behindMoney;

    @ApiModelProperty(value = "所属线路id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "质保期限 单位月")
    private Integer expiration;

    @ApiModelProperty(value = "支付注意")
    private String attention;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "合同部件id 来自设备类型表的id")
    private String assetTypeId;

    @ApiModelProperty(value = "合同部件别名")
    private String  assetTypeAlias;

    @ApiModelProperty(value = "车辆数量")
    private Integer trainAmount;

    @ApiModelProperty(value = "已完成数量")
    private Integer finishAmount;

    @ApiModelProperty(value = "当前车辆")
    private String curTrain;

    @ApiModelProperty(value = "所属修程id")
    private String repairProgramId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "维修所需天数")
    private Integer needDay;

    @ApiModelProperty(value = "保证金类型 字典dicCode=bu_contract_deposit_type 1现金2保函")
    @Dict(dicCode = "bu_contract_deposit_type")
    private Integer depositType;

    @ApiModelProperty(value = "质保金比例% 30表示30%")
    private Integer qualityDepositPercent;

    @ApiModelProperty(value = "质保金类型 字典dicCode=bu_contract_deposit_type 1现金2保函")
    @Dict(dicCode = "bu_contract_deposit_type")
    private Integer qualityDepositType;

    @ApiModelProperty(value = "预付款金额")
    private BigDecimal advancePayment;

    @ApiModelProperty(value = "部件数量")
    private Integer assetAmount;

    @ApiModelProperty(value = "执行税率")
    private Float executeTaxRate;

    @ApiModelProperty(value = "支付间隔 （每个多少列支付一次）")
    private Integer payInterval;

    @ApiModelProperty(value = "计数开始（从第几列开始计算）")
    private Integer payBegin;

    @ApiModelProperty(value = "质保开始日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warrantyStartDate;

    @ApiModelProperty(value = "质保结束日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warrantyFinishDate;

    @ApiModelProperty(value = "是否已退款 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer hasRefunded;

    @ApiModelProperty(value = "是否需要提醒退款 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer needRemindRefund;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "合同厂商名称")
    @TableField(exist = false)
    private String supplierName;

    @ApiModelProperty(value = "所属线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属修程名称")
    @TableField(exist = false)
    private String repairProgramName;

    @ApiModelProperty(value = "进度")
    @TableField(exist = false)
    private String progress;

    @ApiModelProperty(value = "合同总进度")
    @TableField(exist = false)
    private Integer sumProgress;

    @ApiModelProperty(value = "合同设备名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "合同扩展信息")
    @TableField(exist = false)
    private BuContractExtInfo extInfo;

    @ApiModelProperty(value = "合同附件列表")
    @TableField(exist = false)
    private List<BuContractAnnex> annexList;

    @ApiModelProperty(value = "支付记录列表")
    @TableField(exist = false)
    private List<BuContractPay> payList;

    @ApiModelProperty(value = "暂列金记录列表")
    @TableField(exist = false)
    private List<BuContractBakMoney> bakMoneyList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

