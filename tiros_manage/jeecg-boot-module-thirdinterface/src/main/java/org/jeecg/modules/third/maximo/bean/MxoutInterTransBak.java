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
 * maximo输出队列备份
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MXOUT_INTER_TRANS_BAK")
@ApiModel(value = "MxoutInterTrans对象", description = "")
public class MxoutInterTransBak extends Model<MxoutInterTransBak> {
    private static final long serialVersionUID = 1L;

    @TableField("MESSAGEID")
    private String messageid;

    @TableField("ACTION")
    private String action;

    @TableField("IFACENAME")
    private String ifacename;

    @TableField("IFACETBNAME")
    private String ifacetbname;

    @TableField("IMPORTMESSAGE")
    private String importmessage;

    @TableField("EXTSYSNAME")
    private String extsysname;

    @TableField("TRANSLANGUAGE")
    private String translanguage;

    @TableField("TRANSID")
    private Long transid;

    @TableField("INSERTTIME")
    private Date inserttime;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
