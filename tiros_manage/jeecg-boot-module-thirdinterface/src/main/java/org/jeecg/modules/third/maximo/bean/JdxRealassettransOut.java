package org.jeecg.modules.third.maximo.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;

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
@TableName("JDX_REALASSETTRANS_OUT")
@ApiModel(value="JdxRealassetOut对象", description="")
public class JdxRealassettransOut extends Model<JdxRealassettransOut> {

    private static final long serialVersionUID=1L;

    @TableField("ASSETNUM")
    private String assetnum;

    @TableField("CREWGROUP")
    private String crewgroup;

    @TableField("C_REALASSETTRANSID")
    private Double cRealassettransid;

    @TableField("ENTERBY")
    private String enterby;

    @TableField("INITITEMSERIALNUM")
    private String inititemserialnum;

    @TableField("ITEMNUM")
    private String itemnum;

    @TableField("OPERATEMODE")
    private String operatemode;

    @TableField("REALASSETNUM")
    private String realassetnum;

    @TableField("SEQUENCE")
    private Double sequence;

    @TableField("TRANSDATE")
    private Date transdate;

    @TableField("LEAD")
    private String lead;

    @TableField("PERSONGROUP")
    private String persongroup;

    @TableField("SITEID")
    private String siteid;

    @TableField("WONUM")
    private String wonum;

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
