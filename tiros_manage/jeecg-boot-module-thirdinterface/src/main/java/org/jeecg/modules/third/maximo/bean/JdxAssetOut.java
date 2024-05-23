package org.jeecg.modules.third.maximo.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("JDX_ASSET_OUT")
@ApiModel(value="JdxAssetOut对象", description="")
public class JdxAssetOut extends Model<JdxAssetOut> {

    private static final long serialVersionUID=1L;

    @TableField("ASSETNUM")
    private String assetnum;

    @TableField("ASSETTYPE")
    private String assettype;

    @TableField("ASSETUID")
    private Long assetuid;

    @TableField("CHANGEBY")
    private String changeby;

    @TableField("CHANGEDATE")
    private Date changedate;

    @TableField("CHILDREN")
    private Double children;

    @TableField("C_DEPT")
    private String cDept;

    @TableField("C_LINENUM")
    private String cLinenum;

    @TableField("C_PERSONGROUP")
    private String cPersongroup;

    @TableField("C_SPECIALTY")
    private String cSpecialty;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("DISABLED")
    private Double disabled;

    @TableField("FAILURECODE")
    private String failurecode;

    @TableField("GROUPNAME")
    private String groupname;

    @TableField("INSTALLDATE")
    private Date installdate;

    @TableField("LOCATION")
    private String location;

    @TableField("ORGID")
    private String orgid;

    @TableField("PARENT")
    private String parent;

    @TableField("SERIALNUM")
    private String serialnum;

    @TableField("SITEID")
    private String siteid;

    @TableField("STATUS")
    private String status;

    @TableField("STATUSDATE")
    private Date statusdate;

    @TableField("ASSETMETERID")
    private Double assetmeterid;

    @TableField("LASTREADING")
    private String lastreading;

    @TableField("LASTREADINGDATE")
    private Date lastreadingdate;

    @TableField("LINEARASSETMETERID")
    private Double linearassetmeterid;

    @TableField("METERNAME")
    private String metername;

    @TableField("TRANSID")
    private Long transid;

    @TableField("TRANSSEQ")
    private Long transseq;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
