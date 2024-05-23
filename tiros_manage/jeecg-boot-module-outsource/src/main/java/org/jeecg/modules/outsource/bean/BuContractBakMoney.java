package org.jeecg.modules.outsource.bean;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

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

/**
 * <p>
 * 暂列金使用记录
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuContractBakMoney对象", description = "暂列金使用记录")
@TableName("bu_contract_bak_money")
public class BuContractBakMoney extends Model<BuContractBakMoney> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "合同id", required = true)
    @NotBlank
    private String contractId;

    @ApiModelProperty(value = "使用序号 自动累计")
    private Integer useNo;
    @ApiModelProperty(value = "使用名称", required = true)
    @NotBlank
    private String subject;
    @ApiModelProperty(value = "使用金额", required = true)
    @NotNull
    private BigDecimal amount;
    @ApiModelProperty(value = "使用日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date useDate;
    @ApiModelProperty(value = "剩余金额", hidden = true)
    private BigDecimal leftover;
    @ApiModelProperty(value = "使用原因")
    private String reason;
    @ApiModelProperty(value = "办理人")
    private String dealMan;
    @ApiModelProperty(value = "办理日期", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dealDate;
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
