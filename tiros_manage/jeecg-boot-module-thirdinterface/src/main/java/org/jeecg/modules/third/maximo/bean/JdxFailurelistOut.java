package org.jeecg.modules.third.maximo.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("JDX_FAILURELIST_OUT")
@ApiModel(value="JdxFailurelistIface对象", description="")
public class JdxFailurelistOut extends Model<JdxFailurelistOut> {

    private static final long serialVersionUID=1L;

    @TableField("ACTIVE")
    private Double active;

    @TableField("C_FAILURELISTID")
    private Double cFailurelistid;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("FAILTYPE")
    private String failtype;

    @TableField("FAILURECODE")
    private String failurecode;

    @TableField("PARENT")
    private String parent;

    @TableField("SEQUENCE")
    private Double sequence;

    @TableField("SITEID")
    private String siteid;

    @TableField("SPECIALTY")
    private String specialty;

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
