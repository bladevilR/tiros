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
 *
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("JDX_SR_OUT")
@ApiModel(value = "JdxSrOut对象", description = "")
public class JdxSrOut extends Model<JdxSrOut> {

    private static final long serialVersionUID = 1L;

    @TableField("ACTUALFINISH")
    private Date actualfinish;

    @TableField("ACTUALSTART")
    private Date actualstart;

    @TableField("AFFECTEDDATE")
    private Date affecteddate;

    @TableField("ASSETNUM")
    private String assetnum;

    @TableField("ASSETORGID")
    private String assetorgid;

    @TableField("CHANGEBY")
    private String changeby;

    @TableField("CHANGEDATE")
    private Date changedate;

    @TableField("SRCLASS")
    private String srclass;

    @TableField("CREWID")
    private String crewid;

    @TableField("C_ACTUAL")
    private String cActual;

    @TableField("C_CAUSE")
    private String cCause;

    @TableField("C_CONFIRMPERSON")
    private String cConfirmperson;

    @TableField("C_FAILUREAFFECT")
    private String cFailureaffect;

    @TableField("C_FAILURECLASS")
    private String cFailureclass;

    @TableField("C_FAILURESUBSYSTEM")
    private String cFailuresubsystem;

    @TableField("C_FAILURESYSTEM")
    private String cFailuresystem;

    @TableField("C_FAULTRANK")
    private String cFaultrank;

    @TableField("C_FINDBY")
    private String cFindby;

    @TableField("C_FRSG")
    private String cFrsg;

    @TableField("C_LINENUM")
    private String cLinenum;

    @TableField("C_LOCATION")
    private String cLocation;

    @TableField("C_OCCSD")
    private String cOccsd;

    @TableField("C_PERSONGROUP")
    private String cPersongroup;

    @TableField("C_PRODUCTSD")
    private String cProductsd;

    @TableField("C_REMEDY")
    private String cRemedy;

    @TableField("C_STATION")
    private String cStation;

    @TableField("C_TMPREPAIRETIME")
    private Date cTmprepairetime;

    @TableField("C_USEDMATERIAL")
    private String cUsedmaterial;

    @TableField("C_VENDORSUPERVISOR")
    private String cVendorsupervisor;

    @TableField("C_ZDGZ")
    private Double cZdgz;

    @TableField("C_ZHANFASD")
    private String cZhanfasd;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("DESCRIPTION_LONGDESCRIPTION")
    private String descriptionLongdescription;

    @TableField("DESCRIPTION_LONGDESCRIPTION2")
    private String descriptionLongdescription2;

    @TableField("FAILURECODE")
    private String failurecode;

    @TableField("LEAD")
    private String lead;

    @TableField("LOCATION")
    private String location;

    @TableField("PERSONGROUP")
    private String persongroup;

    @TableField("PROBLEMCODE")
    private String problemcode;

    @TableField("REPORTDATE")
    private Date reportdate;

    @TableField("REPORTEDBY")
    private String reportedby;

    @TableField("REPORTEDPHONE")
    private String reportedphone;

    @TableField("RESULTSTATUS")
    private String resultstatus;

    @TableField("SITEID")
    private String siteid;

    @TableField("STATUS")
    private String status;

    @TableField("STATUSDATE")
    private Date statusdate;

    @TableField("SUPERVISOR")
    private String supervisor;

    @TableField("TARGETSTART")
    private Date targetstart;

    @TableField("TICKETID")
    private String ticketid;

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
