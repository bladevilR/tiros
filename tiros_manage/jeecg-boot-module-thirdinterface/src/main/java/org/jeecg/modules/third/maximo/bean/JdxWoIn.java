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
@TableName("JDX_WO_IN")
@ApiModel(value = "JdxWoIn对象", description = "")
public class JdxWoIn extends Model<JdxWoIn> {
    private static final long serialVersionUID = 1L;

    @TableField("ACTFINISH")
    private Date actfinish;

    @TableField("ACTSTART")
    private Date actstart;

    @TableField("ASSETNUM")
    private String assetnum;

    @TableField("CHANGEBY")
    private String changeby;

    @TableField("CHANGEDATE")
    private Date changedate;

    @TableField("CREWID")
    private String crewid;

    @TableField("C_BIGCLASS")
    private String cBigclass;

    @TableField("C_FAILURESUBSYSTEM")
    private String cFailuresubsystem;

    @TableField("C_FAILURESYSTEM")
    private String cFailuresystem;

    @TableField("C_LINECODE")
    private String cLinecode;

    @TableField("C_N_DJCLASS")
    private String cNDjclass;

    @TableField("C_N_ZQCLASS")
    private String cNZqclass;

    @TableField("C_SPECIALTY")
    private String cSpecialty;

    @TableField("C_STATION")
    private String cStation;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("ESTDUR")
    private Float estdur;

    @TableField("FCPROJECTID")
    private String fcprojectid;

    @TableField("FCTASKID")
    private String fctaskid;

    @TableField("LEAD")
    private String lead;

    @TableField("LOCATION")
    private String location;

    @TableField("PARENT")
    private String parent;

    @TableField("PERSONGROUP")
    private String persongroup;

    @TableField("REPORTDATE")
    private Date reportdate;

    @TableField("REPORTEDBY")
    private String reportedby;

    @TableField("SITEID")
    private String siteid;

    @TableField("STATUS")
    private String status;

    @TableField("STATUSDATE")
    private Date statusdate;

    @TableField("SUPERVISOR")
    private String supervisor;

    @TableField("TARGCOMPDATE")
    private Date targcompdate;

    @TableField("TARGSTARTDATE")
    private Date targstartdate;

    @TableField("WONUM")
    private String wonum;

    @TableField("WORKORDERID")
    private Long workorderid;

    @TableField("WORKTYPE")
    private String worktype;

    @TableField("TRANSID")
    private Long transid;

    @TableField("TRANSSEQ")
    private Long transseq;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
