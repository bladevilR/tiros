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
@TableName("JDX_LABTRANS_OUT")
@ApiModel(value = "JdxLabtransOut对象", description = "")
public class JdxLabtransOut extends Model<JdxLabtransOut> {

    private static final long serialVersionUID = 1L;

    @TableField("CONTRACTNUM")
    private String contractnum;

    @TableField("CRAFT")
    private String craft;

    @TableField("EXTERNALREFID")
    private String externalrefid;

    @TableField("FCPROJECTID")
    private String fcprojectid;

    @TableField("FCTASKID")
    private String fctaskid;

    @TableField("LABORCODE")
    private String laborcode;

    @TableField("LABTRANSID")
    private Double labtransid;

    @TableField("ORGID")
    private String orgid;

    @TableField("REFWO")
    private String refwo;

    @TableField("REGULARHRS")
    private Float regularhrs;

    @TableField("SITEID")
    private String siteid;

    @TableField("SKILLLEVEL")
    private String skilllevel;

    @TableField("SOURCESYSID")
    private String sourcesysid;

    @TableField("STARTDATE")
    private Date startdate;

    @TableField("STARTTIME")
    private Date starttime;

    @TableField("TRANSDATE")
    private Date transdate;

    @TableField("TRANSTYPE")
    private String transtype;

    @TableField("VENDOR")
    private String vendor;

    @TableField("TRANSID")
    private Long transid;

    @TableField("TRANSSEQ")
    private Long transseq;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
