package org.jeecg.modules.third.maximo.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 物资库存成本
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("JDX_INVCOST_OUT")
@ApiModel(value = "JdxInvcostOut对象", description = "物资库存成本")
public class JdxInvcostOut extends Model<JdxInvcostOut> {
    private static final long serialVersionUID = 1L;

    @TableField("AVGCOST")
    private Double avgcost;

    @TableField("CONDITIONCODE")
    private String conditioncode;

    @TableField("CONDRATE")
    private Double condrate;

    @TableField("CONTROLACC")
    private String controlacc;

    @TableField("GLACCOUNT")
    private String glaccount;

    @TableField("INVCOSTADJACC")
    private String invcostadjacc;

    @TableField("INVCOSTID")
    private Double invcostid;

    @TableField("ITEMNUM")
    private String itemnum;

    @TableField("ITEMSETID")
    private String itemsetid;

    @TableField("LASTCOST")
    private Double lastcost;

    @TableField("LOCATION")
    private String location;

    @TableField("ORGID")
    private String orgid;

    @TableField("SHRINKAGEACC")
    private String shrinkageacc;

    @TableField("SITEID")
    private String siteid;

    @TableField("STDCOST")
    private Double stdcost;

    @TableField("TRANSID")
    private Long transid;

    @TableField("TRANSSEQ")
    private Long transseq;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
