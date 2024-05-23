package org.jeecg.modules.basemanage.org.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户标签
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysUserTag对象", description = "用户标签")
@TableName("sys_user_tag")
public class SysUserTag extends Model<SysUserTag> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "标签名")
    private String tagTitle;

    @ApiModelProperty(value = "标签id")
    private String tagId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
