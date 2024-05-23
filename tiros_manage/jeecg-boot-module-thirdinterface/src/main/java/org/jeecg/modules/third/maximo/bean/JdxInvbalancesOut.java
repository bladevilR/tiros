package org.jeecg.modules.third.maximo.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 物资库存
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("JDX_INVBALANCES_OUT")
@ApiModel(value = "JdxInvbalancesOut对象", description = "")
public class JdxInvbalancesOut extends Model<JdxInvbalancesOut> {

    private static final long serialVersionUID = 1L;

    @TableField("BINNUM")
    private String binnum;

    @TableField("CONDITIONCODE")
    private String conditioncode;

    @TableField("CURBAL")
    private Double curbal;

    @TableField("C_AVGCOST")
    private Double cAvgcost;

    @TableField("INVBALANCESID")
    private Integer invbalancesid;

    @TableField("ITEMNUM")
    private String itemnum;

    @TableField("ITEMSETID")
    private String itemsetid;

    @TableField("LOCATION")
    private String location;

    @TableField("LOTNUM")
    private String lotnum;

    @TableField("ORGID")
    private String orgid;

    @TableField("SITEID")
    private String siteid;

    /**
     * C_CONVERSION = 1代表转换系数
     * 物资有采购单位、发放单位，例如吨和千克，米和厘米
     * 按吨采购，按千克方法，C_CONVERSION = 1000
     */
    @TableField("C_CONVERSION")
    private Double cConversion;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("IN19")
    private String in19;

    @TableField("IN20")
    private String in20;

    @TableField("IN21")
    private String in21;

    @TableField("ISSUEUNIT")
    private String issueunit;

    @TableField("ORDERUNIT")
    private String orderunit;

    @TableField("STATUS")
    private String status;

    @TableField("STATUSDATE")
    private Date statusdate;

    @TableField("C_INVENTORYORG")
    private String cInventoryorg;

    @TableField("TRANSID")
    private Long transid;

    @TableField("TRANSSEQ")
    private Long transseq;

    @TableField("INSERTDATE")
    private Date insertdate;


    /**
     * 上一次库存量
     */
    @TableField(exist = false)
    private Double lastStock;

    /**
     * 库存变动量，新增为正，减少为负
     */
    @TableField(exist = false)
    private Double stockChange;

    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private String transAction;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
