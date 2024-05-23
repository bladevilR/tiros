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
@TableName("JDX_LOCATIONS_OUT")
@ApiModel(value="JdxLocationsOut对象", description="")
public class JdxLocationsOut extends Model<JdxLocationsOut> {

    private static final long serialVersionUID=1L;

    @TableField("CHANGEBY")
    private String changeby;

    @TableField("CHANGEDATE")
    private Date changedate;

    @TableField("CHILDREN")
    private Double children;

    @TableField("C_FEATURE")
    private String cFeature;

    @TableField("C_LOCTYPE")
    private String cLoctype;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("HASCHILDREN")
    private Double haschildren;

    @TableField("LOCATION")
    private String location;

    @TableField("ORGID")
    private String orgid;

    @TableField("PARENT")
    private String parent;

    @TableField("SITEID")
    private String siteid;

    @TableField("STATUS")
    private String status;

    @TableField("TYPE")
    private String type;

    @TableField("TRANSID")
    private Long transid;

    @TableField("TRANSSEQ")
    private Long transseq;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
