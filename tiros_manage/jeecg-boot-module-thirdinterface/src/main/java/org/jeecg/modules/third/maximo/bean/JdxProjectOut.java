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
 * 财务项目
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("JDX_PROJECT_OUT")
@ApiModel(value = "JdxProjectOut对象", description = "")
public class JdxProjectOut extends Model<JdxProjectOut> {

    private static final long serialVersionUID = 1L;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("DISABLED")
    private Double disabled;

    @TableField("ENDDATE")
    private Date enddate;

    @TableField("FCSTATUS")
    private String fcstatus;

    @TableField("FCTYPE")
    private String fctype;

    @TableField("FINCNTRLID")
    private String fincntrlid;

    @TableField("ORGID")
    private String orgid;

    @TableField("PARENTFINCNTRLID")
    private String parentfincntrlid;

    @TableField("PROJECTID")
    private String projectid;

    @TableField("PROJECTTYPE")
    private String projecttype;

    @TableField("SITEID")
    private String siteid;

    @TableField("STARTDATE")
    private Date startdate;

    @TableField("TASKID")
    private String taskid;

    @TableField("TASKLEVEL")
    private Double tasklevel;

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
