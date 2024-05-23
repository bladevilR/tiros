package org.jeecg.modules.third.maximo.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("JDX_MATUSETRANS_OUT")
@ApiModel(value = "JdxMatusetransOut对象", description = "")
public class JdxMatusetransOut extends Model<JdxMatusetransOut> {

    private static final long serialVersionUID = 1L;

    @TableField("BINNUM")
    private String binnum;

    @TableField("C_DEPT")
    private String cDept;

    @TableField("C_INVENTORYORG")
    private String cInventoryorg;

    @TableField("C_STATUS")
    private String cStatus;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("ENTERBY")
    private String enterby;

    @TableField("EXTERNALREFID")
    private String externalrefid;

    @TableField("FCPROJECTID")
    private String fcprojectid;

    @TableField("FCTASKID")
    private String fctaskid;

    @TableField("ISSUETO")
    private String issueto;

    @TableField("ISSUETYPE")
    private String issuetype;

    @TableField("ITEMNUM")
    private String itemnum;

    @TableField("ITEMSETID")
    private String itemsetid;

    @TableField("LINECOST")
    private Double linecost;

    @TableField("LINETYPE")
    private String linetype;

    @TableField("LOCATION")
    private String location;

    @TableField("LOTNUM")
    private String lotnum;

    @TableField("MATUSETRANSID")
    private Double matusetransid;

    @TableField("OWNERSYSID")
    private String ownersysid;

    @TableField("POSITIVEQUANTITY")
    private Double positivequantity;

    @TableField("QUANTITY")
    private Double quantity;

    @TableField("REFWO")
    private String refwo;

    @TableField("SENDERSYSID")
    private String sendersysid;

    @TableField("SITEID")
    private String siteid;

    @TableField("SOURCESYSID")
    private String sourcesysid;

    @TableField("STORELOC")
    private String storeloc;

    @TableField("TOSITEID")
    private String tositeid;

    @TableField("TRANSDATE")
    private Date transdate;

    @TableField("UNITCOST")
    private Double unitcost;

    @TableField("TRANSID")
    private Long transid;

    @TableField("TRANSSEQ")
    private Long transseq;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
