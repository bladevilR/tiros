package org.jeecg.modules.third.maximo.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("JDX_REALASSET_OUT")
@ApiModel(value = "JdxRealassetOut对象", description = "")
public class JdxRealassetOut extends Model<JdxRealassetOut> {

    private static final long serialVersionUID = 1L;

    @TableField("ASSETNUM")
    private String assetnum;

    @TableField("CALLASTDATE")
    private Date callastdate;

    @TableField("CALPERIOD")
    private Double calperiod;

    @TableField("CENTER")
    private String center;

    @TableField("CUSTODIAN")
    private String custodian;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("ISSUEUNIT")
    private String issueunit;

    @TableField("ITEMNUM")
    private String itemnum;

    @TableField("LINENUM")
    private String linenum;

    @TableField("LOCATION")
    private String location;

    @TableField("PERSONGROUP")
    private String persongroup;

    @TableField("PURCHASEDATE")
    private Date purchasedate;

    @TableField("PURCHASEPRICE")
    private Double purchaseprice;

    @TableField("REALASSETNUM")
    private String realassetnum;

    @TableField("REALASSETTYPE")
    private String realassettype;

    @TableField("RECEIVEDATE")
    private Date receivedate;

    @TableField("RECEIVER")
    private String receiver;

    @TableField("SITEID")
    private String siteid;

    @TableField("STATUS")
    private String status;

    @TableField("STATUSDATE")
    private Date statusdate;

    @TableField("WORKSHOP")
    private String workshop;

    @TableField("TRANSID")
    private Long transid;

    @TableField("TRANSSEQ")
    private Long transseq;


    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private String transAction;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
